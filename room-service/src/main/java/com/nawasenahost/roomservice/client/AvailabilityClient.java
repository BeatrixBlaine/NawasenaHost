package com.nawasenahost.roomservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "availability-client")
public interface AvailabilityClient {

    @PostMapping("/api/inventory/initialize")
    void initializeInventory(
            @RequestParam int hotelId,
            @RequestParam int totalRooms
    );

}
