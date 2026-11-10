package com.nawasenahost.adminpanel.exception;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // token is missing or expired, back to login
    @ExceptionHandler(SessionExpiredException.class)
    public String handleSessionExpired(HttpSession session) {
        session.invalidate();
        return "redirect:/login-page?expired";
    }

    // anything a controller did not handle by itself
    @ExceptionHandler(ApiException.class)
    public String handleApiException(ApiException e, HttpSession session, Model model) {

        if (e.getError().getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            session.invalidate();
            return "redirect:/login-page?expired";
        }

        model.addAttribute("error", e.getError());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {

        model.addAttribute("error", new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred.",
                System.currentTimeMillis()));

        return "error";
    }
}
