package com.nawasenahost.availabilityservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service")
public interface RoomClient {

    @GetMapping("/api/rooms/{roomId}")
    ResponseEntity<Void> getRoom(@PathVariable int roomId);

}
