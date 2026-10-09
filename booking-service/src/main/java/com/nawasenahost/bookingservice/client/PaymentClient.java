package com.nawasenahost.bookingservice.client;

import com.nawasenahost.bookingservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/create-payment")
    PaymentResponse createPayment(
            @RequestParam("bookingId") int bookingId
    );

}
