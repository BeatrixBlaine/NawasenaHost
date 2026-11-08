package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.exception.ErrorResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Controller
public class BookingController {

    private final WebClient webClient;

    public BookingController(WebClient webClient) {
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

    @GetMapping("/booking-page")
    public String bookingPage(HttpSession session,
                              Model model,
                              @RequestParam("roomId") int roomId) {

        SessionData data = getSessionData(session);

        model.addAttribute("bookingRequest", new BookingRequest());
        model.addAttribute("hotelId", data.hotelId());
        model.addAttribute("roomId", roomId);

        return "book";
    }

    @PostMapping("/create-booking")
    public String createBooking(Model model,
                                HttpSession session,
                                @Valid @ModelAttribute("bookingRequest") BookingRequest bookingRequest,
                                BindingResult bindingResult) {

        SessionData data = getSessionData(session);

        if (bindingResult.hasErrors()) {
            return "book";
        }

        try {
            BookingResponse bookingResponse = webClient.post()
                    .uri("/api/customers/create-new-booking")
                    .headers(headers -> headers.setBearerAuth(data.token()))
                    .bodyValue(bookingRequest)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();

            model.addAttribute("bookingsuccess", bookingResponse);

            return "book";

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
            return "book";
        }
    }

    @PostMapping("/cancel-pending-booking")
    public String cancelPendingBooking(HttpSession session,
                                       Model model,
                                       @RequestParam("bookingId") int bookingId) {

        SessionData data = getSessionData(session);

        try {
            webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/bookings/cancel-pending-booking")
                            .queryParam("bookingId", bookingId)
                            .build())
                    .headers(headers -> headers.setBearerAuth(data.token()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

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
            return "book";
        }

        return "redirect:/dashboard";
    }

}
