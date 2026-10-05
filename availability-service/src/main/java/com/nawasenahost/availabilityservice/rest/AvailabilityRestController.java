package com.nawasenahost.availabilityservice.rest;

import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.entity.Availability;
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
}
