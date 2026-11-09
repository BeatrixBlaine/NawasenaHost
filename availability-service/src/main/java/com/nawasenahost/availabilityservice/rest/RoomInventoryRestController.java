package com.nawasenahost.availabilityservice.rest;

import com.nawasenahost.availabilityservice.entity.RoomInventory;
import com.nawasenahost.availabilityservice.service.RoomInventoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomInventoryRestController {

    private final RoomInventoryService roomInventoryService;

    public RoomInventoryRestController(RoomInventoryService roomInventoryService) {
        this.roomInventoryService = roomInventoryService;
    }

    @PostMapping("/inventory/initialize")
    public String initializeInventory(@RequestParam int hotelId,
                                      @RequestParam int totalRooms) {

        roomInventoryService.initializeInventory(hotelId, totalRooms);

        return "Hotel inventory initialized successfully for hotel ID: " + hotelId;
    }

    @GetMapping("/inventory/{hotelId}")
    public List<RoomInventory> getHotelInventory(@PathVariable int hotelId,
                                                 @RequestParam LocalDate startDate,
                                                 @RequestParam LocalDate endDate) {

        return roomInventoryService.findByHotelIdAndDateRange(
                hotelId,
                startDate,
                endDate
        );
    }
}
