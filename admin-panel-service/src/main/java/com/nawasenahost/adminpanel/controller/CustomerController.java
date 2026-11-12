package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.CustomerForm;
import com.nawasenahost.adminpanel.dto.CustomerResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.BookingService;
import com.nawasenahost.adminpanel.service.CustomerService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
public class CustomerController {

    private final SessionService sessionService;
    private final CustomerService customerService;
    private final BookingService bookingService;

    public CustomerController(SessionService sessionService,
                              CustomerService customerService,
                              BookingService bookingService) {
        this.sessionService = sessionService;
        this.customerService = customerService;
        this.bookingService = bookingService;
    }

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) String q,
                            HttpSession session,
                            Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "customers");
        model.addAttribute("q", q);

        try {
            model.addAttribute("customers", customerService.findAll(sessionData, q));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "customers";
    }

    @GetMapping("/customers/{customerId}")
    public String customerDetail(@PathVariable int customerId,
                                 HttpSession session,
                                 Model model) {

        SessionData sessionData = sessionService.get(session);

        CustomerResponse customer = customerService.findById(sessionData, customerId);

        CustomerForm form = new CustomerForm();
        form.setFirstName(customer.firstName());
        form.setLastName(customer.lastName());
        form.setEmail(customer.email());
        form.setPhone(customer.phone());

        model.addAttribute("activePage", "customers");
        model.addAttribute("customer", customer);
        model.addAttribute("form", form);

        try {
            List<BookingSummary> bookings = bookingService.findByCustomer(sessionData, customerId).stream()
                    .sorted(Comparator.comparingInt(BookingSummary::bookingId).reversed())
                    .toList();

            model.addAttribute("bookings", bookings);
        } catch (ApiException e) {
            model.addAttribute("bookingError", e.getError().getMessage());
        }

        return "customer-detail";
    }

    @PostMapping("/customers/{customerId}")
    public String updateCustomer(@PathVariable int customerId,
                                 @Valid @ModelAttribute("form") CustomerForm form,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "customers");
            model.addAttribute("customer", customerService.findById(sessionData, customerId));
            return "customer-detail";
        }

        try {
            customerService.update(sessionData, customerId, form);
            redirectAttributes.addFlashAttribute("success", "Customer updated.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/customers/" + customerId;
    }
}
