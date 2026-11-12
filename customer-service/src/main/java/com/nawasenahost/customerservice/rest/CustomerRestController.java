package com.nawasenahost.customerservice.rest;

import com.nawasenahost.customerservice.dto.BookingResponse;
import com.nawasenahost.customerservice.dto.BookingRequest;
import com.nawasenahost.customerservice.dto.CustomerData;
import com.nawasenahost.customerservice.entity.Customer;
import com.nawasenahost.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerRestController {

    private final CustomerService customerService;

    @Autowired
    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/customers/hotel/{hotelId}")
    public List<Customer> getCustomersByHotelId(@PathVariable int hotelId,
                                                @RequestParam(required = false) String q) {
        return customerService.findByHotelId(hotelId, q);
    }

    @PutMapping("/customers/assign-hotel")
    public String assignHotel(@RequestParam int hotelId) {
        return "Customers updated: " + customerService.assignHotel(hotelId);
    }

    @GetMapping("/customers/{customerId}")
    public Customer getCustomer(@PathVariable int customerId) {
        return customerService.findById(customerId);
    }

    @PostMapping("/customers")
    public Customer addCustomer(@Valid @RequestBody CustomerData customerData) {
        return customerService.save(customerData);
    }

    @PutMapping("/customers/{customerId}")
    public Customer updateCustomer(@PathVariable int customerId,
                               @Valid @RequestBody CustomerData customerData) {
        return customerService.update(customerId, customerData);
    }

    @DeleteMapping("/customers/{customerId}")
    public String deleteCustomer(@PathVariable int customerId) {
        customerService.deleteById(customerId);
        return "User with Id: " + customerId + " deleted";
    }

    @PostMapping("/customers/create-new-booking")
    public BookingResponse createNewBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        return customerService.createNewBooking(bookingRequest);
    }
}
