package com.nawasenahost.paymentservice.rest;

import com.nawasenahost.paymentservice.client.BookingClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignTestController {

    private final BookingClient bookingClient;

    public FeignTestController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping("/test/booking")
    public String testGetBooking() {
        bookingClient.getBooking(59);
        return "payment-service successfully calls booking-service";
    }

}
