package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingReport;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.DailyAmount;
import com.nawasenahost.adminpanel.dto.DailyCount;
import com.nawasenahost.adminpanel.dto.OccupancyDay;
import com.nawasenahost.adminpanel.dto.OccupancyReport;
import com.nawasenahost.adminpanel.dto.PaymentReport;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AvailabilityService;
import com.nawasenahost.adminpanel.service.BookingService;
import com.nawasenahost.adminpanel.service.PaymentService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("d MMM");

    private final SessionService sessionService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final AvailabilityService availabilityService;

    public DashboardController(SessionService sessionService,
                               BookingService bookingService,
                               PaymentService paymentService,
                               AvailabilityService availabilityService) {
        this.sessionService = sessionService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "30") int range,
                            HttpSession session,
                            Model model) {

        SessionData sessionData = sessionService.get(session);

        // only these ranges are offered on the page
        int days = (range == 7 || range == 90) ? range : 30;

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(days - 1L);

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("range", days);

        // a hotel has to exist before there is something to show (a new ADMIN has none yet)
        if (sessionData.hotelId() == 0) {
            model.addAttribute("noHotel", true);
            return "dashboard";
        }

        // every block is loaded on its own, so one failing service does not blank the page
        try {
            BookingReport report = bookingService.getReport(sessionData, from, today);

            model.addAttribute("bookingReport", report);
            model.addAttribute("pendingBookings", report.byStatus().getOrDefault("PENDING", 0L));
            model.addAttribute("statusLabels", report.byStatus().keySet());
            model.addAttribute("statusValues", report.byStatus().values());
            model.addAttribute("sourceLabels", report.bySource().keySet());
            model.addAttribute("sourceValues", report.bySource().values());
            model.addAttribute("checkInLabels", report.dailyCheckIns().stream()
                    .map(DailyCount::date).map(d -> d.format(LABEL)).toList());
            model.addAttribute("checkInValues", report.dailyCheckIns().stream()
                    .map(DailyCount::count).toList());
        } catch (ApiException e) {
            model.addAttribute("bookingError", e.getError().getMessage());
        }

        try {
            PaymentReport report = paymentService.getReport(sessionData, from, today);

            model.addAttribute("paymentReport", report);
            model.addAttribute("revenueLabels", report.dailyRevenue().stream()
                    .map(DailyAmount::date).map(d -> d.format(LABEL)).toList());
            model.addAttribute("revenueValues", report.dailyRevenue().stream()
                    .map(DailyAmount::amount).toList());
            model.addAttribute("methodLabels", report.revenueByMethod().keySet());
            model.addAttribute("methodValues", report.revenueByMethod().values());
        } catch (ApiException e) {
            model.addAttribute("paymentError", e.getError().getMessage());
        }

        try {
            // looking forward, so the hotel sees how full the next month is
            OccupancyReport occupancy = availabilityService.getOccupancy(sessionData, today, today.plusDays(29));

            model.addAttribute("occupancy", occupancy);
            model.addAttribute("occupancyLabels", occupancy.days().stream()
                    .map(OccupancyDay::date).map(d -> d.format(LABEL)).toList());
            model.addAttribute("occupancyValues", occupancy.days().stream()
                    .map(OccupancyDay::rate).toList());
        } catch (ApiException e) {
            model.addAttribute("occupancyError", e.getError().getMessage());
        }

        try {
            List<BookingSummary> recent = bookingService.findAll(sessionData).stream()
                    .sorted(Comparator.comparingInt(BookingSummary::bookingId).reversed())
                    .limit(6)
                    .toList();

            model.addAttribute("recentBookings", recent);
        } catch (ApiException e) {
            model.addAttribute("recentError", e.getError().getMessage());
        }

        return "dashboard";
    }
}
