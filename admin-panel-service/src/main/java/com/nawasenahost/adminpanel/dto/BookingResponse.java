package com.nawasenahost.adminpanel.dto;

public record BookingResponse(int bookingId,
                              int customerId,
                              int hotelId,
                              int roomId,
                              String status) {
}
