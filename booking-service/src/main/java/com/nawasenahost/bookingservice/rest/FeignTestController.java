package com.nawasenahost.bookingservice.rest;

import com.nawasenahost.bookingservice.client.HotelClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class FeignTestController {

    private final HotelClient hotelClient;

    public FeignTestController(HotelClient hotelClient) {
        this.hotelClient = hotelClient;
    }

    @GetMapping("/hotel/{hotelId}")
    public String testHotelCall(@PathVariable int hotelId) {
        hotelClient.getHotel(hotelId);
        return "Booking successfully called Hotel Service!";
    }
}