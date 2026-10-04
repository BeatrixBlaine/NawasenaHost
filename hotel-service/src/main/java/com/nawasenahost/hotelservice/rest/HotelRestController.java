package com.nawasenahost.hotelservice.rest;

import com.nawasenahost.hotelservice.dto.HotelRequest;
import com.nawasenahost.hotelservice.entity.Hotel;
import com.nawasenahost.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelRestController {

    private final HotelService hotelService;

    public HotelRestController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels")
    public List<Hotel> getHotels() {
        return hotelService.findAll();
    }

    @GetMapping("/hotels/{hotelId}")
    public Hotel getHotel(@PathVariable int hotelId) {
        return hotelService.findById(hotelId);
    }

    @PostMapping("/hotels")
    public Hotel addHotel(@Valid @RequestBody HotelRequest hotelRequest) {
        return hotelService.save(hotelRequest);
    }

    @PutMapping("/hotels/{hotelId}")
    public Hotel updateHotel(@PathVariable int hotelId,
                             @Valid @RequestBody HotelRequest hotelRequest) {
        return hotelService.update(hotelId, hotelRequest);
    }

    @DeleteMapping("/hotels/{hotelId}")
    public String deleteHotel(@PathVariable int hotelId) {
        hotelService.deleteById(hotelId);
        return "Hotel deleted with id: " + hotelId;
    }

}
