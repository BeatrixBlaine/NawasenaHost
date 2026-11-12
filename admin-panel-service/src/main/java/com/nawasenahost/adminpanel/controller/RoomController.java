package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.RoomForm;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.RoomService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RoomController {

    private final SessionService sessionService;
    private final RoomService roomService;

    public RoomController(SessionService sessionService,
                          RoomService roomService) {
        this.sessionService = sessionService;
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public String rooms(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "rooms");

        try {
            model.addAttribute("rooms", roomService.findAll(sessionData));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "room";
    }

    @GetMapping("/rooms/new")
    public String newRoom(Model model) {

        RoomForm form = new RoomForm();
        form.setCapacity(2);

        model.addAttribute("activePage", "rooms");
        model.addAttribute("form", form);

        return "room-form";
    }

    @PostMapping("/rooms")
    public String addRoom(@Valid @ModelAttribute("form") RoomForm form,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "rooms");

        if (bindingResult.hasErrors()) {
            return "room-form";
        }

        try {
            RoomResponse room = roomService.save(sessionData, form);
            redirectAttributes.addFlashAttribute("success", "Room " + room.roomNumber() + " added.");
            return "redirect:/rooms";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "room-form";
        }
    }

    @GetMapping("/rooms/{roomId}/edit")
    public String editRoom(@PathVariable int roomId,
                           HttpSession session,
                           Model model) {

        SessionData sessionData = sessionService.get(session);

        RoomResponse room = roomService.findById(sessionData, roomId);

        RoomForm form = new RoomForm();
        form.setRoomId(room.roomId());
        form.setRoomNumber(room.roomNumber());
        form.setRoomType(room.roomType());
        form.setPrice(room.price());
        form.setCapacity(room.capacity());
        form.setDescription(room.description());

        model.addAttribute("activePage", "rooms");
        model.addAttribute("form", form);

        return "room-form";
    }

    @PostMapping("/rooms/{roomId}")
    public String updateRoom(@PathVariable int roomId,
                             @Valid @ModelAttribute("form") RoomForm form,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "rooms");
        form.setRoomId(roomId);

        if (bindingResult.hasErrors()) {
            return "room-form";
        }

        try {
            roomService.update(sessionData, roomId, form);
            redirectAttributes.addFlashAttribute("success", "Room " + form.getRoomNumber() + " updated.");
            return "redirect:/rooms";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "room-form";
        }
    }

    @PostMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable int roomId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        try {
            roomService.delete(sessionData, roomId);
            redirectAttributes.addFlashAttribute("success", "Room deleted.");
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getError().getMessage());
        }

        return "redirect:/rooms";
    }
}
