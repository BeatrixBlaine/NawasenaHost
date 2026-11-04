package com.nawasenahost.customerservice.rest;

import com.nawasenahost.customerservice.client.BookingClient;
import com.nawasenahost.customerservice.dto.BookingRequest;
import com.nawasenahost.customerservice.dto.BookingResponse;
import com.nawasenahost.customerservice.dto.CreateNewBookingRequest;
import com.nawasenahost.customerservice.dto.CustomerRequest;
import com.nawasenahost.customerservice.entity.Customer;
import com.nawasenahost.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/customers/{customerId}")
    public Customer getCustomer(@PathVariable int customerId) {
        return customerService.findById(customerId);
    }

    @PostMapping("/customers")
    public Customer addCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.save(customerRequest);
    }

    @PutMapping("/customers/{customerId}")
    public Customer updateCustomer(@PathVariable int customerId,
                               @Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.update(customerId, customerRequest);
    }

    @DeleteMapping("/customers/{customerId}")
    public String deleteCustomer(@PathVariable int customerId) {
        customerService.deleteById(customerId);
        return "User with Id: " + customerId + " deleted";
    }

    @PostMapping("/customers/create-new-booking")
    public BookingResponse createNewBooking(@Valid @RequestBody CreateNewBookingRequest request) {
        return customerService.createNewBooking(request.getCustomerRequest(), request.getBookingRequest());
    }
}
