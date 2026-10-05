package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.entity.Booking;

import java.util.List;

public interface BookingService {

    List<Booking> findAll();
    Booking findById(int id);
    Booking save(BookingRequest bookingRequest);
    void deleteById(int id);
    Booking update(int id, BookingRequest bookingRequest);

}
