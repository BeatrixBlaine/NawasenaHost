package com.nawasenahost.availabilityservice.rest;

import com.nawasenahost.availabilityservice.service.RoomInventoryService;
import com.nawasenahost.availabilityservice.dto.OccupancyReport;
import com.nawasenahost.availabilityservice.entity.RoomInventory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> initializeInventory(@RequestParam int hotelId,
                                                      @RequestParam int totalRooms) {

        roomInventoryService.initializeInventory(hotelId, totalRooms);

        return ResponseEntity.ok(
                "Hotel inventory initialized successfully for hotel ID: " + hotelId
        );
    }

    @GetMapping("/inventory/hotel/{hotelId}")
    public List<RoomInventory> getInventory(@PathVariable int hotelId,
                                            @RequestParam(required = false) LocalDate from,
                                            @RequestParam(required = false) LocalDate to) {

        LocalDate startDate = from != null ? from : LocalDate.now();
        LocalDate endDate = to != null ? to : startDate.plusDays(29);

        return roomInventoryService.findByHotelId(hotelId, startDate, endDate);
    }

    @GetMapping("/inventory/hotel/{hotelId}/occupancy")
    public OccupancyReport getOccupancy(@PathVariable int hotelId,
                                        @RequestParam(required = false) LocalDate from,
                                        @RequestParam(required = false) LocalDate to) {

        LocalDate startDate = from != null ? from : LocalDate.now();
        LocalDate endDate = to != null ? to : startDate.plusDays(29);

        return roomInventoryService.getOccupancy(hotelId, startDate, endDate);
    }
}
