package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public record BookingResponse(int bookingId,
                              int customerId,
                              int hotelId,
                              int roomId,
                              String status) {
}
