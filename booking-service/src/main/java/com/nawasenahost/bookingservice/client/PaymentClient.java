package com.nawasenahost.bookingservice.client;

import com.nawasenahost.bookingservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/create-payment")
    PaymentResponse createPayment(
            @RequestParam("bookingId") int bookingId
    );

    @PutMapping("/api/payments/cancel-payment-by-booking-id")
    void cancelPaymentByBookingId(
            @RequestParam("bookingId") int bookingId
    );

    @PutMapping("/api/payments/refund-by-booking-id")
    void refundPaymentByBookingId(
            @RequestParam("bookingId") int bookingId
    );

    @GetMapping("/api/payments/booking/{bookingId}")
    PaymentResponse getPaymentByBookingId(
            @PathVariable("bookingId") int bookingId
    );
}
