package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.ErrorResponse;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Controller
public class RoomController {

    private final WebClient webClient;

    public RoomController(WebClient webClient) {
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

    @GetMapping("/rooms")
    public String getRooms(HttpSession session,
                           Model model) {

        SessionData data = getSessionData(session);

        try {

            List<RoomResponse> rooms = webClient.get()
                    .uri("/api/rooms/{hotelId}", data.hotelId())
                    .headers(headers -> headers.setBearerAuth(data.token()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RoomResponse>>() {})
                    .block();

            model.addAttribute("rooms", rooms);

        } catch (WebClientResponseException e) {

            model.addAttribute("error", new ErrorResponse(e.getResponseBodyAsString()));

        }

        return "room";
    }

}
