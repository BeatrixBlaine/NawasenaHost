package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.RoomService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomController {

    private final SessionService sessionService;
    private final RoomService roomService;

    public RoomController(SessionService sessionService,
                          RoomService roomService) {
        this.sessionService = sessionService;
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public String rooms(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "rooms");

        try {
            model.addAttribute("rooms", roomService.findAll(sessionData));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "room";
    }
}
