package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.RoomResponse;
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

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model) {

        String token = (String) session.getAttribute("accessToken");
        String role = (String) session.getAttribute("role");
        int authId = (int) session.getAttribute("authId");
        int hotelId = (int) session.getAttribute("hotelId");
        String tokenType = (String) session.getAttribute("tokenType");

        List<RoomResponse> rooms = webClient.get()
                .uri("/api/rooms/{hotelId}", hotelId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoomResponse>>() {
                })
                .block();

        model.addAttribute("role", role);
        model.addAttribute("rooms", rooms);

        return "dashboard";
    }

}
