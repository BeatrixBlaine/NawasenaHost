package com.nawasenahost.availabilityservice.rest;

import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.dto.RoomResponse;
import com.nawasenahost.availabilityservice.entity.Availability;
import com.nawasenahost.availabilityservice.entity.AvailabilityStatus;
import com.nawasenahost.availabilityservice.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AvailabilityRestController {

    private final AvailabilityService availabilityService;

    public AvailabilityRestController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availabilities")
    public List<Availability> getAvailabilities() {
        return availabilityService.findAll();
    }

    @GetMapping("/availabilities/{availabilityId}")
    public Availability getAvailability(@PathVariable int availabilityId){
        return availabilityService.findById(availabilityId);
    }

    @PostMapping("/availabilities")
    public Availability addAvailability(@Valid @RequestBody AvailabilityRequest availabilityRequest) {
        return availabilityService.save(availabilityRequest);
    }

    @PutMapping("/availabilities/{availabilityId}")
    public Availability updateAvailability(@PathVariable int availabilityId,
                                           @Valid @RequestBody AvailabilityRequest availabilityRequest) {
        return availabilityService.update(availabilityId, availabilityRequest);
    }

    @DeleteMapping("/availabilities/{availabilityId}")
    public String deleteAvailability(@PathVariable int availabilityId) {
        availabilityService.deleteById(availabilityId);
        return "Availability with id: " + availabilityId + " deleted";
    }

    // endpoint to be called by admin-panel-service
    @GetMapping("/availabilities/hotel/{hotelId}/available-rooms")
    public List<RoomResponse> getAvailableRooms(
            @PathVariable int hotelId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {
        return availabilityService.findAvailableRooms(hotelId, checkInDate, checkOutDate);
    }

    // endpoints to be called by booking-service
    @GetMapping("/availabilities/check")
    public boolean checkAvailability(
            @RequestParam int roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {

        return availabilityService.isAvailable(
                roomId,
                checkInDate,
                checkOutDate
        );
    }

    @PostMapping("/availabilities/book")
    public List<Availability> createBooking(
            @RequestParam int roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {

        return availabilityService.createBooking(roomId, checkInDate, checkOutDate);
    }

    // called by booking-service when a booking is cancelled
    @DeleteMapping("/availabilities/release")
    public String releaseBooking(
            @RequestParam int roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {

        return "Nights released: " + availabilityService.releaseBooking(roomId, checkInDate, checkOutDate);
    }

    // endpoints to be called by admin-panel-service
    @GetMapping("/availabilities/hotel/{hotelId}/blocks")
    public List<Availability> getBlocks(
            @PathVariable int hotelId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        LocalDate startDate = from != null ? from : LocalDate.now();
        LocalDate endDate = to != null ? to : startDate.plusDays(30);

        return availabilityService.findBlocks(hotelId, startDate, endDate);
    }

    @PostMapping("/availabilities/block")
    public String blockRoom(
            @RequestParam int roomId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "MAINTENANCE") AvailabilityStatus status) {

        return "Nights blocked: " + availabilityService.blockRoom(roomId, startDate, endDate, status);
    }

    @DeleteMapping("/availabilities/unblock")
    public String unblockRoom(
            @RequestParam int roomId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return "Nights opened: " + availabilityService.unblockRoom(roomId, startDate, endDate);
    }
}
