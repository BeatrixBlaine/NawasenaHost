package com.nawasenahost.customerservice.service;

import com.nawasenahost.customerservice.audit.AuditPublisher;
import com.nawasenahost.customerservice.client.BookingClient;
import com.nawasenahost.customerservice.dto.BookingData;
import com.nawasenahost.customerservice.dto.BookingRequest;
import com.nawasenahost.customerservice.dto.BookingResponse;
import com.nawasenahost.customerservice.dto.CustomerData;
import com.nawasenahost.customerservice.entity.Customer;
import com.nawasenahost.customerservice.exception.CustomerNotFoundException;
import com.nawasenahost.customerservice.repository.CustomerRepository;
import com.nawasenahost.customerservice.security.TenantGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingClient bookingClient;
    private final TenantGuard tenantGuard;
    private final AuditPublisher auditPublisher;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               BookingClient bookingClient,
                               TenantGuard tenantGuard,
                               AuditPublisher auditPublisher) {
        this.customerRepository = customerRepository;
        this.bookingClient = bookingClient;
        this.tenantGuard = tenantGuard;
        this.auditPublisher = auditPublisher;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findByHotelId(int hotelId, String q) {

        tenantGuard.assertHotel(hotelId);

        if (q == null || q.isBlank()) {
            return customerRepository.findByHotelIdOrderByCustomerIdDesc(hotelId);
        }

        return customerRepository.search(hotelId, q.trim());
    }

    @Override
    public Customer findById(int id) {

        Optional<Customer> tempCustomer = customerRepository.findById(id);

        Customer theCustomer;
        if(tempCustomer.isPresent()) {
            theCustomer = tempCustomer.get();
        } else {
            throw new CustomerNotFoundException("Customer not found with Id: " + id);
        }

        // customers without a hotel yet (hotelId 0) can still be read by every staff member
        if (theCustomer.getHotelId() != 0) {
            tenantGuard.assertHotel(theCustomer.getHotelId());
        }

        return theCustomer;
    }

    @Override
    public Customer save(CustomerData customerData) {

        Customer tempCustomer = new Customer();
        tempCustomer.setFirstName(customerData.getFirstName());
        tempCustomer.setLastName(customerData.getLastName());
        tempCustomer.setEmail(customerData.getEmail());
        tempCustomer.setPhone(customerData.getPhone());
        tempCustomer.setHotelId(resolveHotelId(customerData.getHotelId()));
        tempCustomer.setCreatedAt(LocalDateTime.now());

        return customerRepository.save(tempCustomer);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        customerRepository.deleteById(id);
    }

    @Override
    public Customer update(int id, CustomerData customerData) {

        Customer tempCustomer = findById(id);
        tempCustomer.setFirstName(customerData.getFirstName());
        tempCustomer.setLastName(customerData.getLastName());
        tempCustomer.setEmail(customerData.getEmail());
        tempCustomer.setPhone(customerData.getPhone());

        return customerRepository.save(tempCustomer);
    }

    // admin sends the hotel, manager and staff always use the hotel of their account
    private int resolveHotelId(Integer requestedHotelId) {

        if (tenantGuard.isAdmin() || tenantGuard.isServiceCall()) {
            return requestedHotelId == null ? 0 : requestedHotelId;
        }

        return tenantGuard.currentHotelId();
    }

    @Override
    public int assignHotel(int hotelId) {

        int updated = 0;

        for (Customer customer : customerRepository.findByHotelIdOrderByCustomerIdDesc(0)) {
            customer.setHotelId(hotelId);
            customerRepository.save(customer);
            updated++;
        }

        auditPublisher.publish(hotelId, "CUSTOMER_ASSIGN_HOTEL", "CUSTOMER", hotelId,
                updated + " customers without a hotel assigned to hotel " + hotelId);

        return updated;
    }

    // the customer is saved first because the booking service reads it back, so a failed booking removes the customer again
    @Override
    public BookingResponse createNewBooking(BookingRequest bookingRequest) {

        tenantGuard.assertHotel(bookingRequest.getBookingData().getHotelId());

        Customer tempCustomer = new Customer();
        tempCustomer.setFirstName(bookingRequest.getCustomerData().getFirstName());
        tempCustomer.setLastName(bookingRequest.getCustomerData().getLastName());
        tempCustomer.setEmail(bookingRequest.getCustomerData().getEmail());
        tempCustomer.setPhone(bookingRequest.getCustomerData().getPhone());
        tempCustomer.setHotelId(bookingRequest.getBookingData().getHotelId());
        tempCustomer.setCreatedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(tempCustomer);

        bookingRequest.getBookingData().setCustomerId(savedCustomer.getCustomerId());

        BookingResponse createdBooking;

        try {
            createdBooking = bookingClient.addBooking(bookingRequest.getBookingData());
        } catch (RuntimeException e) {
            customerRepository.deleteById(savedCustomer.getCustomerId());
            throw e;
        }

        auditPublisher.publish(
                createdBooking.getHotelId(),
                "BOOKING_CREATED",
                "BOOKING",
                createdBooking.getBookingId(),
                "Booking for room " + createdBooking.getRoomId() + " created for customer " + savedCustomer.getCustomerId());

        return createdBooking;
    }
}
