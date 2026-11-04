package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.ErrorResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/book")
    public String createBook(Model model,
                             HttpSession session) {

        SessionData data = getSessionData(session);

        try {
            BookingResponse
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                model.addAttribute(
                        "error",
                        new ErrorResponse(e.getResponseBodyAsString())
                );
            }
            return "book";
        }
    }

}
