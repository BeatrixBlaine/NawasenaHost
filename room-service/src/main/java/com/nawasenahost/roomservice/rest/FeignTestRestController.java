package com.nawasenahost.roomservice.rest;

import com.nawasenahost.roomservice.client.HotelClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignTestRestController {

    private final HotelClient hotelClient;

    public FeignTestRestController(HotelClient hotelClient) {
        this.hotelClient = hotelClient;
    }

    @GetMapping("/test/hotelcalls")
    public String feignCalls() {
        hotelClient.getHotel(1);
        return "hotel feign calls successfully";
    }
}
