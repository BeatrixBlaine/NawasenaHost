package com.nawasenahost.bookingservice.rest;

import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingRestController {

    private final BookingService bookingService;

    public BookingRestController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings")
    public List<Booking> getBookings() {
        return bookingService.findAll();
    }

    @GetMapping("/bookings/{bookingId}")
    public Booking getBooking(@PathVariable int bookingId) {
        return bookingService.findById(bookingId);
    }

    @PostMapping("/bookings")
    public Booking addBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.save(bookingRequest);
    }

    @PutMapping("/bookings/{bookingId}")
    public Booking updateBooking(@PathVariable int bookingId,
                                 @Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.update(bookingId, bookingRequest);
    }

    @DeleteMapping("/bookings/{bookingId}")
    public String deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteById(bookingId);
        return "Booking with Id: " + bookingId + " deleted";
    }

    @PutMapping("/bookings/{bookingId}/confirm-booking")
    public Booking confirmBooking(@PathVariable int bookingId) {
        return bookingService.confirmBooking(bookingId);
    }
}
