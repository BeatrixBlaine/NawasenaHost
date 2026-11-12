package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.HotelForm;
import com.nawasenahost.adminpanel.dto.HotelResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.HotelService;
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

// only an ADMIN reaches these pages, the rule is in SecurityConfig
@Controller
public class HotelController {

    private final SessionService sessionService;
    private final HotelService hotelService;

    public HotelController(SessionService sessionService,
                           HotelService hotelService) {
        this.sessionService = sessionService;
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels")
    public String hotels(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "hotels");

        try {
            model.addAttribute("hotelList", hotelService.findAll(sessionData));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "hotels";
    }

    @GetMapping("/hotels/new")
    public String newHotel(Model model) {

        model.addAttribute("activePage", "hotels");
        model.addAttribute("form", new HotelForm());
        model.addAttribute("hotelId", null);

        return "hotel-form";
    }

    @PostMapping("/hotels")
    public String addHotel(@Valid @ModelAttribute("form") HotelForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "hotels");
        model.addAttribute("hotelId", null);

        if (bindingResult.hasErrors()) {
            return "hotel-form";
        }

        try {
            HotelResponse hotel = hotelService.save(sessionData, form);

            // the first hotel becomes the active one right away
            if (sessionData.hotelId() == 0) {
                sessionService.save(session, sessionData.withHotel(hotel.id(), hotel.name()));
            }

            redirectAttributes.addFlashAttribute("success", "Hotel " + hotel.name() + " created. Add its rooms next.");
            return "redirect:/hotels";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "hotel-form";
        }
    }

    @GetMapping("/hotels/{hotelId}/edit")
    public String editHotel(@PathVariable int hotelId,
                            HttpSession session,
                            Model model) {

        SessionData sessionData = sessionService.get(session);

        HotelResponse hotel = hotelService.findById(sessionData, hotelId);

        HotelForm form = new HotelForm();
        form.setName(hotel.name());
        form.setDescription(hotel.description());
        form.setAddress(hotel.address());
        form.setCity(hotel.city());
        form.setCountry(hotel.country());
        form.setPostalCode(hotel.postalCode());
        form.setPhone(hotel.phone());
        form.setEmail(hotel.email());

        model.addAttribute("activePage", "hotels");
        model.addAttribute("form", form);
        model.addAttribute("hotelId", hotelId);

        return "hotel-form";
    }

    @PostMapping("/hotels/{hotelId}")
    public String updateHotel(@PathVariable int hotelId,
                              @Valid @ModelAttribute("form") HotelForm form,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "hotels");
        model.addAttribute("hotelId", hotelId);

        if (bindingResult.hasErrors()) {
            return "hotel-form";
        }

        try {
            HotelResponse hotel = hotelService.update(sessionData, hotelId, form);

            // the name in the top bar follows the change
            if (sessionData.hotelId() == hotelId) {
                sessionService.save(session, sessionData.withHotel(hotelId, hotel.name()));
            }

            redirectAttributes.addFlashAttribute("success", "Hotel " + hotel.name() + " updated.");
            return "redirect:/hotels";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "hotel-form";
        }
    }

    @PostMapping("/hotels/{hotelId}/delete")
    public String deleteHotel(@PathVariable int hotelId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        // the hotel that is open right now cannot be deleted
        if (sessionData.hotelId() == hotelId) {
            redirectAttributes.addFlashAttribute("error", "Switch to another hotel before deleting this one.");
            return "redirect:/hotels";
        }

        try {
            hotelService.delete(sessionData, hotelId);
            redirectAttributes.addFlashAttribute("success", "Hotel deleted.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/hotels";
    }

    // the hotel switcher in the sidebar
    @PostMapping("/switch-hotel")
    public String switchHotel(@RequestParam int hotelId,
                              @RequestParam(defaultValue = "/dashboard") String returnTo,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            HotelResponse hotel = hotelService.findById(sessionData, hotelId);
            sessionService.save(session, sessionData.withHotel(hotel.id(), hotel.name()));
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        // only local pages, never an address from outside
        return "redirect:" + (returnTo.startsWith("/") && !returnTo.startsWith("//") && !returnTo.contains("\\")
                ? returnTo : "/dashboard");
    }
}
