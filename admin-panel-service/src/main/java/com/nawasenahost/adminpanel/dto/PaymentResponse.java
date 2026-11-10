package com.nawasenahost.adminpanel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// payment-service serializes the id as "paymentid"
public record PaymentResponse(int paymentid,
                              int bookingId,
                              BigDecimal amount,
                              String currency,
                              String paymentStatus,
                              String paymentMethod,
                              String paymentSource,
                              LocalDateTime paidAt) {
}
