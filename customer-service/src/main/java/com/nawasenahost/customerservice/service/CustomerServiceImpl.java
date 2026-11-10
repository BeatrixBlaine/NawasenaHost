package com.nawasenahost.customerservice.service;

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

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingClient bookingClient;
    private final TenantGuard tenantGuard;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               BookingClient bookingClient,
                               TenantGuard tenantGuard) {
        this.customerRepository = customerRepository;
        this.bookingClient = bookingClient;
        this.tenantGuard = tenantGuard;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
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

        return theCustomer;
    }

    @Override
    public Customer save(CustomerData customerData) {

        Customer tempCustomer = new Customer();
        tempCustomer.setFirstName(customerData.getFirstName());
        tempCustomer.setLastName(customerData.getLastName());
        tempCustomer.setEmail(customerData.getEmail());
        tempCustomer.setPhone(customerData.getPhone());

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

    // if the booking fails, the newly saved customer is rolled back too
    @Override
    @Transactional
    public BookingResponse createNewBooking(BookingRequest bookingRequest) {

        tenantGuard.assertHotel(bookingRequest.getBookingData().getHotelId());

        Customer tempCustomer = new Customer();
        tempCustomer.setFirstName(bookingRequest.getCustomerData().getFirstName());
        tempCustomer.setLastName(bookingRequest.getCustomerData().getLastName());
        tempCustomer.setEmail(bookingRequest.getCustomerData().getEmail());
        tempCustomer.setPhone(bookingRequest.getCustomerData().getPhone());

        Customer savedCustomer = customerRepository.save(tempCustomer);

        bookingRequest.getBookingData().setCustomerId(savedCustomer.getCustomerId());

        BookingResponse createdBooking = bookingClient.addBooking(bookingRequest.getBookingData());

        return createdBooking;
    }
}
