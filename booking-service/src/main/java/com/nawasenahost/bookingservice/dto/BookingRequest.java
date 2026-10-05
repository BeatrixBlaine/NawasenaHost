package com.nawasenahost.bookingservice.dto;

import com.nawasenahost.bookingservice.entity.BookingStatus;

import java.time.LocalDate;

public class BookingRequest {

    private int bookingId;

    private int userId;

    private int hotelId;

    private int roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int guestCount;

    private BookingStatus status;

}
