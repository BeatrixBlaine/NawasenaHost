package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingSummary(int bookingId,
                             int customerId,
                             int hotelId,
                             int roomId,
                             LocalDate checkInDate,
                             LocalDate checkOutDate,
                             int guestCount,
                             String status,
                             String source,
                             String externalReference,
                             LocalDateTime createdAt,
                             LocalDateTime cancelledAt,
                             String cancelReason) {
}
