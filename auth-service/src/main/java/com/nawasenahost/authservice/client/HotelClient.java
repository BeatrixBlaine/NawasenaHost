package com.nawasenahost.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hotel-service")
public interface HotelClient {

    @GetMapping("/api/hotels/{hotelId}")
    void getHotel(@PathVariable int hotelId);

}
