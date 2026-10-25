package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Controller
public class DashboardController {

    private final WebClient webClient;

    public DashboardController(WebClient webClient) {
        this.webClient = webClient;
    }

    public SessionData getSessionData(HttpSession session) {
        return new SessionData(
                (String) session.getAttribute("accessToken"),
                (String) session.getAttribute("role"),
                (int) session.getAttribute("authId"),
                (int) session.getAttribute("hotelId"),
                (String) session.getAttribute("tokenType")
        );
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model) {

        SessionData data = getSessionData(session);

        model.addAttribute("role", data.role());

        return "dashboard";
    }

}
