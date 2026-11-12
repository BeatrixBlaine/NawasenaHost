package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.AvailabilityBlock;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AvailabilityService;
import com.nawasenahost.adminpanel.service.RoomService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AvailabilityController {

    private final SessionService sessionService;
    private final AvailabilityService availabilityService;
    private final RoomService roomService;

    public AvailabilityController(SessionService sessionService,
                                  AvailabilityService availabilityService,
                                  RoomService roomService) {
        this.sessionService = sessionService;
        this.availabilityService = availabilityService;
        this.roomService = roomService;
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

    // two weeks calendar: one row for every room, one column for every night
    @GetMapping("/inventory")
    public String inventory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            HttpSession session,
            Model model) {

        SessionData sessionData = sessionService.get(session);

        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = start.plusDays(14);

        model.addAttribute("activePage", "inventory");
        model.addAttribute("from", start);
        model.addAttribute("previous", start.minusDays(14));
        model.addAttribute("next", end);

        List<LocalDate> dates = new ArrayList<>();

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            dates.add(date);
        }

        model.addAttribute("dates", dates);

        try {
            model.addAttribute("rooms", roomService.findAll(sessionData));

            Map<String, String> cells = new HashMap<>();

            for (AvailabilityBlock block : availabilityService.findBlocks(sessionData, start, end)) {
                cells.put(block.roomId() + "|" + block.date(), block.status());
            }

            model.addAttribute("cells", cells);
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        try {
            model.addAttribute("occupancy", availabilityService.getOccupancy(sessionData, start, end.minusDays(1)));
        } catch (ApiException e) {
            model.addAttribute("occupancyError", e.getError().getMessage());
        }

        return "inventory";
    }

    @PostMapping("/inventory/block")
    public String block(@RequestParam int roomId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "MAINTENANCE") String status,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            availabilityService.block(sessionData, roomId, startDate, endDate, status);
            redirectAttributes.addFlashAttribute("success", "Room closed from " + startDate + " until " + endDate + ".");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/inventory?from=" + startDate;
    }

    @PostMapping("/inventory/unblock")
    public String unblock(@RequestParam int roomId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            availabilityService.unblock(sessionData, roomId, startDate, endDate);
            redirectAttributes.addFlashAttribute("success", "Room opened again from " + startDate + " until " + endDate + ".");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/inventory?from=" + startDate;
    }
}
