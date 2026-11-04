package com.nawasenahost.customerservice.service;

import com.nawasenahost.customerservice.dto.BookingRequest;
import com.nawasenahost.customerservice.dto.BookingResponse;
import com.nawasenahost.customerservice.dto.CustomerRequest;
import com.nawasenahost.customerservice.entity.Customer;
import jakarta.validation.Valid;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();
    Customer findById(int id);
    Customer save(CustomerRequest customerRequest);
    void deleteById(int id);
    Customer update(int id, CustomerRequest customerRequest);

    BookingResponse createNewBooking(@Valid CustomerRequest customerRequest,
                                     @Valid BookingRequest bookingRequest);
}
