package com.nawasenahost.availabilityservice.client;

import com.nawasenahost.availabilityservice.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "room-service")
public interface RoomClient {

    @GetMapping("/api/rooms/single-room/{roomId}")
    RoomResponse getRoom(@PathVariable int roomId);

    @GetMapping("/api/rooms/{hotelId}")
    List<RoomResponse> getRooms(@PathVariable int hotelId);

}
