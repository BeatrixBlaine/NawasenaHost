package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.PaymentReport;
import com.nawasenahost.adminpanel.dto.PaymentResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.PaymentService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class PaymentController {

    private final SessionService sessionService;
    private final PaymentService paymentService;

    public PaymentController(SessionService sessionService,
                             PaymentService paymentService) {
        this.sessionService = sessionService;
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public String payments(@RequestParam(required = false) String status,
                           HttpSession session,
                           Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "payments");
        model.addAttribute("status", status);

        try {
            List<PaymentResponse> payments = paymentService.findAll(sessionData);

            if (status != null && !status.isBlank()) {
                payments = payments.stream()
                        .filter(payment -> status.equals(payment.paymentStatus()))
                        .toList();
            }

            model.addAttribute("payments", payments.stream()
                    .sorted(Comparator.comparingInt(PaymentResponse::paymentid).reversed())
                    .toList());
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        // this month, so the totals on top match what the hotel expects to see
        try {
            LocalDate today = LocalDate.now();
            PaymentReport report = paymentService.getReport(sessionData, today.withDayOfMonth(1), today);
            model.addAttribute("report", report);
        } catch (ApiException e) {
            model.addAttribute("reportError", e.getError().getMessage());
        }

        return "payments";
    }
}
