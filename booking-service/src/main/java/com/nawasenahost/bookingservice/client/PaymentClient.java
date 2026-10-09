package com.nawasenahost.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/create-payment")
    void createPayment(
            @RequestParam("bookingId") int bookingId
    );

    @PutMapping("/api/payments/cancel-payment-by-booking-id")
    void cancelPaymentByBookingId(
            @RequestParam("bookingId") int bookingId
    );

}
