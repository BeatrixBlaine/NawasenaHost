package com.nawasenahost.paymentservice.client;

import com.nawasenahost.paymentservice.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service")
public interface RoomClient {

    @GetMapping("/api/rooms/{roomId}")
    RoomResponse getRoom(@PathVariable int roomId);

}
