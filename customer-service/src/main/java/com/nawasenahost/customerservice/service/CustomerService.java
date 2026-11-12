package com.nawasenahost.customerservice.service;

import com.nawasenahost.customerservice.dto.BookingData;
import com.nawasenahost.customerservice.dto.BookingRequest;
import com.nawasenahost.customerservice.dto.BookingResponse;
import com.nawasenahost.customerservice.dto.CustomerData;
import com.nawasenahost.customerservice.entity.Customer;
import jakarta.validation.Valid;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();
    List<Customer> findByHotelId(int hotelId, String q);
    Customer findById(int id);
    Customer save(CustomerData customerData);
    void deleteById(int id);
    Customer update(int id, CustomerData customerData);

    int assignHotel(int hotelId);

    BookingResponse createNewBooking(@Valid BookingRequest bookingRequest);
}
