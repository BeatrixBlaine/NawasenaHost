package com.nawasenahost.paymentservice.client;

import com.nawasenahost.paymentservice.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/api/bookings/{bookingId}")
    BookingResponse getBooking(@PathVariable int bookingId);

    @PutMapping("/api/bookings/{bookingId}/confirm-booking")
    ResponseEntity<Void> confirmBooking(@PathVariable int bookingId);

}
