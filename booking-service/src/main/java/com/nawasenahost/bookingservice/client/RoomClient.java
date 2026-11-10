package com.nawasenahost.bookingservice.client;

import com.nawasenahost.bookingservice.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service")
public interface RoomClient {

    @GetMapping("/api/rooms/single-room/{roomId}")
    RoomResponse getRoom(@PathVariable int roomId);

}
