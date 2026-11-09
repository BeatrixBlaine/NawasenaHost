package com.nawasenahost.availabilityservice.rest;

import com.nawasenahost.availabilityservice.service.RoomInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoomInventoryRestController {

    private final RoomInventoryService roomInventoryService;

    public RoomInventoryRestController(RoomInventoryService roomInventoryService) {
        this.roomInventoryService = roomInventoryService;
    }

    @PostMapping("/inventory/initialize")
    public ResponseEntity<String> initializeInventory(@RequestParam int hotelId,
                                                      @RequestParam int totalRooms) {

        roomInventoryService.initializeInventory(hotelId, totalRooms);

        return ResponseEntity.ok(
                "Hotel inventory initialized successfully for hotel ID: " + hotelId
        );
    }
}
