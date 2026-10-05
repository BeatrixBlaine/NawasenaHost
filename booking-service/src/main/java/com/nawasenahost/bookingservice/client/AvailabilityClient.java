package com.nawasenahost.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "availability-service")
public interface AvailabilityClient {

    @GetMapping("/api/availabilities/check")
    boolean checkAvailability(
            @RequestParam int roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate
    );

    @PostMapping("/api/availabilities/book")
    void createBooking(
            @RequestParam int roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate
    );

}
