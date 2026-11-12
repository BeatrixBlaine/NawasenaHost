package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.HotelResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.HotelService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.List;

// values every page needs for the sidebar and the top bar
@ControllerAdvice
public class LayoutAdvice {

    private final SessionService sessionService;
    private final HotelService hotelService;

    @Value("${app.currency:IDR}")
    private String currency;

    public LayoutAdvice(SessionService sessionService,
                        HotelService hotelService) {
        this.sessionService = sessionService;
        this.hotelService = hotelService;
    }

    @ModelAttribute("currency")
    public String currency() {
        return currency;
    }

    @ModelAttribute("today")
    public LocalDate today() {
        return LocalDate.now();
    }

    @ModelAttribute("me")
    public SessionData me(HttpSession session) {
        return sessionService.find(session);
    }

    // only an ADMIN can switch between hotels
    @ModelAttribute("hotels")
    public List<HotelResponse> hotels(HttpSession session) {

        SessionData sessionData = sessionService.find(session);

        if (sessionData == null || !"ADMIN".equals(sessionData.role())) {
            return List.of();
        }

        try {
            return hotelService.findAll(sessionData);
        } catch (ApiException e) {
            return List.of();
        }
    }
}
