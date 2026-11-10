package com.nawasenahost.roomservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "availability-service")
public interface AvailabilityClient {

    @PostMapping("/api/inventory/initialize")
    void initializeInventory(
            @RequestParam("hotelId") int hotelId,
            @RequestParam("totalRooms") int totalRooms
    );

}
