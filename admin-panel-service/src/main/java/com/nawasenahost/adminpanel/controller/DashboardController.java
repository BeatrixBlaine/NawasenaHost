package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AvailabilityService;
import com.nawasenahost.adminpanel.service.BookingService;
import com.nawasenahost.adminpanel.service.RoomService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final SessionService sessionService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final AvailabilityService availabilityService;

    public DashboardController(SessionService sessionService,
                               RoomService roomService,
                               BookingService bookingService,
                               AvailabilityService availabilityService) {
        this.sessionService = sessionService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("role", sessionData.role());

        // every block is loaded on its own, so one failing service does not blank the page
        try {
            List<RoomResponse> rooms = roomService.findAll(sessionData);
            model.addAttribute("totalRooms", rooms.size());

            LocalDate today = LocalDate.now();
            int available = availabilityService
                    .findAvailableRooms(sessionData, today, today.plusDays(1))
                    .size();

            model.addAttribute("occupiedTonight", rooms.size() - available);
            model.addAttribute("availableTonight", available);
        } catch (ApiException e) {
            model.addAttribute("roomError", e.getError().getMessage());
        }

        try {
            List<BookingSummary> bookings = bookingService.findAll(sessionData);

            long pending = bookings.stream().filter(b -> "PENDING".equals(b.status())).count();
            long confirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.status())).count();

            model.addAttribute("pendingBookings", pending);
            model.addAttribute("confirmedBookings", confirmed);
            model.addAttribute("recentBookings", bookings.stream().limit(5).toList());
        } catch (ApiException e) {
            model.addAttribute("bookingError", e.getError().getMessage());
        }

        return "dashboard";
    }
}
