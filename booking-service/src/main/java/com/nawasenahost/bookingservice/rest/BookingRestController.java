package com.nawasenahost.bookingservice.rest;

import com.nawasenahost.bookingservice.dto.BookingReport;
import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.dto.BookingResponse;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/bookings/hotel/{hotelId}")
    public List<Booking> getBookingsByHotelId(@PathVariable int hotelId) {
        return bookingService.findByHotelId(hotelId);
    }

    @GetMapping("/bookings/hotel/{hotelId}/customer/{customerId}")
    public List<Booking> getBookingsByCustomer(@PathVariable int hotelId,
                                               @PathVariable int customerId) {
        return bookingService.findByHotelIdAndCustomerId(hotelId, customerId);
    }

    @GetMapping("/bookings/hotel/{hotelId}/report")
    public BookingReport getReport(@PathVariable int hotelId,
                                   @RequestParam(required = false) LocalDate from,
                                   @RequestParam(required = false) LocalDate to) {

        LocalDate endDate = to != null ? to : LocalDate.now();
        LocalDate startDate = from != null ? from : endDate.minusDays(29);

        return bookingService.getReport(hotelId, startDate, endDate);
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable int bookingId,
                                 @RequestParam(required = false) String reason) {
        return bookingService.cancelBooking(bookingId, reason);
    }

    @PutMapping("/bookings/{bookingId}/complete")
    public Booking completeBooking(@PathVariable int bookingId) {
        return bookingService.completeBooking(bookingId);
    }

    @GetMapping("/bookings/{bookingId}")
    public Booking getBooking(@PathVariable int bookingId) {
        return bookingService.findById(bookingId);
    }

    @PostMapping("/bookings")
    public BookingResponse addBooking(@Valid @RequestBody BookingRequest bookingRequest) {
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

    @DeleteMapping("/bookings/cancel-pending-booking")
    public Booking cancelPendingBooking(@RequestParam int bookingId) {
        return bookingService.cancelPendingBooking(bookingId);
    }


}
