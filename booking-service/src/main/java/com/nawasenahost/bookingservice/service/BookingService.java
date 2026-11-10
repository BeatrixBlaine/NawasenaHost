package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.dto.BookingResponse;
import com.nawasenahost.bookingservice.entity.Booking;

import java.util.List;

public interface BookingService {

    List<Booking> findAll();
    List<Booking> findByHotelId(int hotelId);
    Booking findById(int id);
    BookingResponse save(BookingRequest bookingRequest);
    void deleteById(int id);
    Booking update(int id, BookingRequest bookingRequest);
    Booking confirmBooking(int bookingId);
    Booking cancelPendingBooking(int bookingId);

}
