package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public record BookingSummary(int bookingId,
                             int customerId,
                             int hotelId,
                             int roomId,
                             LocalDate checkInDate,
                             LocalDate checkOutDate,
                             int guestCount,
                             String status) {
}
