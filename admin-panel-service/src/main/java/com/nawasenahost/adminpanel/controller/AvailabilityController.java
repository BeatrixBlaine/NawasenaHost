package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AvailabilityService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AvailabilityController {

    private final SessionService sessionService;
    private final AvailabilityService availabilityService;

    public AvailabilityController(SessionService sessionService,
                                  AvailabilityService availabilityService) {
        this.sessionService = sessionService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availabilities")
    public String availabilities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            HttpSession session,
            Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "availabilities");
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);

        // the form was not submitted yet
        if (checkInDate == null || checkOutDate == null) {
            return "availability";
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            model.addAttribute("error", "Check-out date must be after check-in date");
            return "availability";
        }

        try {
            model.addAttribute("rooms", availabilityService
                    .findAvailableRooms(sessionData, checkInDate, checkOutDate));
            model.addAttribute("searched", true);
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "availability";
    }
}
