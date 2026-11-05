package com.nawasenahost.customerservice.client;

import com.nawasenahost.customerservice.dto.BookingData;
import com.nawasenahost.customerservice.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @PostMapping("/api/bookings")
    BookingResponse addBooking(@RequestBody BookingData bookingData);

}
