package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingData;
import com.nawasenahost.adminpanel.dto.BookingForm;
import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.CustomerData;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AvailabilityService;
import com.nawasenahost.adminpanel.service.BookingService;
import com.nawasenahost.adminpanel.service.CustomerService;
import com.nawasenahost.adminpanel.service.RoomService;
import com.nawasenahost.adminpanel.service.PaymentService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class BookingController {

    private final SessionService sessionService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final AvailabilityService availabilityService;
    private final CustomerService customerService;
    private final RoomService roomService;

    public BookingController(SessionService sessionService,
                             BookingService bookingService,
                             PaymentService paymentService,
                             AvailabilityService availabilityService,
                             CustomerService customerService,
                             RoomService roomService) {
        this.sessionService = sessionService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.availabilityService = availabilityService;
        this.customerService = customerService;
        this.roomService = roomService;
    }

    @GetMapping("/bookings")
    public String bookings(@RequestParam(required = false) String status,
                           @RequestParam(required = false) String q,
                           HttpSession session,
                           Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "bookings");
        model.addAttribute("status", status);
        model.addAttribute("q", q);

        try {
            List<BookingSummary> bookings = bookingService.findAll(sessionData).stream()
                    .sorted(Comparator.comparingInt(BookingSummary::bookingId).reversed())
                    .toList();

            if (status != null && !status.isBlank()) {
                bookings = bookings.stream()
                        .filter(booking -> status.equals(booking.status()))
                        .toList();
            }

            // search on booking number, OTA reference or source
            if (q != null && !q.isBlank()) {
                String query = q.trim().toLowerCase();

                bookings = bookings.stream()
                        .filter(booking -> String.valueOf(booking.bookingId()).equals(query)
                                || (booking.externalReference() != null
                                && booking.externalReference().toLowerCase().contains(query))
                                || (booking.source() != null && booking.source().toLowerCase().contains(query)))
                        .toList();
            }

            model.addAttribute("bookings", bookings);
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "bookings";
    }

    @GetMapping("/bookings/{bookingId}")
    public String bookingDetail(@PathVariable int bookingId,
                                HttpSession session,
                                Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "bookings");

        BookingSummary booking = bookingService.findById(sessionData, bookingId);

        model.addAttribute("booking", booking);

        // customer name and room are a nice to have, the page still works without them
        try {
            model.addAttribute("customer", customerService.findById(sessionData, booking.customerId()));
        } catch (ApiException e) {
            model.addAttribute("customer", null);
        }

        try {
            model.addAttribute("room", roomService.findById(sessionData, booking.roomId()));
        } catch (ApiException e) {
            model.addAttribute("room", null);
        }

        try {
            model.addAttribute("payment", paymentService.findByBookingId(sessionData, bookingId));
        } catch (ApiException e) {
            model.addAttribute("paymentError", e.getError().getMessage());
        }

        return "booking-detail";
    }

    @GetMapping("/booking-page")
    public String bookingPage(@RequestParam int roomId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                              HttpSession session,
                              Model model) {

        SessionData sessionData = sessionService.get(session);

        BookingForm form = new BookingForm();
        form.setRoomId(roomId);
        form.setCheckInDate(checkInDate);
        form.setCheckOutDate(checkOutDate);

        return prepareBookingPage(sessionData, form, model);
    }

    @PostMapping("/create-booking")
    public String createBooking(@Valid @ModelAttribute("form") BookingForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        if (bindingResult.hasErrors()) {
            return prepareBookingPage(sessionData, form, model);
        }

        if (!form.getCheckOutDate().isAfter(form.getCheckInDate())) {
            model.addAttribute("error", "Check-out date must be after check-in date");
            return prepareBookingPage(sessionData, form, model);
        }

        CustomerData customerData = new CustomerData();
        customerData.setFirstName(form.getFirstName());
        customerData.setLastName(form.getLastName());
        customerData.setEmail(form.getEmail());
        customerData.setPhone(form.getPhone());

        BookingData bookingData = new BookingData();
        bookingData.setCustomerId(0);
        bookingData.setHotelId(sessionData.hotelId());
        bookingData.setRoomId(form.getRoomId());
        bookingData.setCheckInDate(form.getCheckInDate());
        bookingData.setCheckOutDate(form.getCheckOutDate());
        bookingData.setGuestCount(form.getGuestCount());
        bookingData.setSource("DIRECT");

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setCustomerData(customerData);
        bookingRequest.setBookingData(bookingData);

        try {
            BookingResponse response = bookingService.create(sessionData, bookingRequest);

            redirectAttributes.addFlashAttribute("success",
                    "Booking #" + response.getBookingId() + " created. Waiting for payment.");

            return "redirect:/bookings/" + response.getBookingId();
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return prepareBookingPage(sessionData, form, model);
        }
    }

    @PostMapping("/bookings/{bookingId}/confirm-payment")
    public String confirmPayment(@PathVariable int bookingId,
                                 @RequestParam int paymentId,
                                 @RequestParam String paymentMethod,
                                 @RequestParam String paymentSource,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            paymentService.confirm(sessionData, paymentId, paymentMethod, paymentSource);
            redirectAttributes.addFlashAttribute("success", "Payment confirmed, booking is now confirmed.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/bookings/" + bookingId;
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable int bookingId,
                                @RequestParam(required = false) String reason,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            BookingSummary cancelled = bookingService.cancel(sessionData, bookingId, reason);
            redirectAttributes.addFlashAttribute("success", "Booking #" + cancelled.bookingId() + " cancelled.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/bookings/" + bookingId;
    }

    @PostMapping("/bookings/{bookingId}/complete")
    public String completeBooking(@PathVariable int bookingId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            bookingService.complete(sessionData, bookingId);
            redirectAttributes.addFlashAttribute("success", "Guest checked out, booking completed.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/bookings/" + bookingId;
    }

    // loads the room shown on top of the form, only a room that is still available is shown
    private String prepareBookingPage(SessionData sessionData, BookingForm form, Model model) {

        model.addAttribute("activePage", "availabilities");
        model.addAttribute("form", form);

        try {
            RoomResponse room = availabilityService
                    .findAvailableRooms(sessionData, form.getCheckInDate(), form.getCheckOutDate())
                    .stream()
                    .filter(r -> r.roomId() == form.getRoomId())
                    .findFirst()
                    .orElse(null);

            if (room == null) {
                model.addAttribute("error", "This room is no longer available for the selected dates");
            }

            model.addAttribute("room", room);
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "book";
    }
}
