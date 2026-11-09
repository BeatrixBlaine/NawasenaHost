package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.nawasenahost.adminpanel.exception.ErrorResponse;

import java.time.LocalDate;

@Controller
public class AvailabilityController {

    private final WebClient webClient;

    public AvailabilityController(WebClient webClient) {
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

    @GetMapping("/availabilities")
    public String getAvailability() {
        return "availability";
    }

    @PostMapping("/availabilities/check")
    public String checkAvailability(@RequestParam("roomId") int roomId,
                                    @RequestParam("checkInDate") LocalDate checkInDate,
                                    @RequestParam("checkOutDate") LocalDate checkOutDate,
                                    HttpSession session,
                                    Model model) {

        SessionData data = getSessionData(session);

        try {
            Boolean isAvailable = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/availabilities/check")
                            .queryParam("roomId", roomId)
                            .queryParam("checkInDate", checkInDate)
                            .queryParam("checkOutDate", checkOutDate)
                            .build())
                    .headers(headers -> headers.setBearerAuth(data.token()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (Boolean.TRUE.equals(isAvailable)) {
                return "redirect:/booking-page?roomId=" + roomId
                        + "&checkInDate=" + checkInDate
                        + "&checkOutDate=" + checkOutDate;
            }

            model.addAttribute("isAvailable", isAvailable);
            return "availability";

        } catch (WebClientResponseException e) {
            ErrorResponse error;

            try {
                error = e.getResponseBodyAs(ErrorResponse.class);
            } catch (Exception ignored) {
                error = null;
            }

            if (error == null) {
                error = new ErrorResponse(
                        e.getStatusCode().value(),
                        "An unexpected error occurred.",
                        System.currentTimeMillis()
                );
            }

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return "redirect:/login-page";
            }

            model.addAttribute("error", error);
            return "availability";
        } catch (Exception e) {
            model.addAttribute("error",
                    "Unable to check availability. Please try again.");
            return "availability";
        }
    }

}
