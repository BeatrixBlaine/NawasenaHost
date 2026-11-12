package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.dto.UserForm;
import com.nawasenahost.adminpanel.dto.UserResponse;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.SessionService;
import com.nawasenahost.adminpanel.service.UserService;
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

// ADMIN manages every user, MANAGER only the STAFF of the own hotel (auth-service checks it again)
@Controller
public class UserController {

    private final SessionService sessionService;
    private final UserService userService;

    public UserController(SessionService sessionService,
                          UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "users");

        try {
            model.addAttribute("users", userService.findAll(sessionData));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(HttpSession session, Model model) {

        SessionData sessionData = sessionService.get(session);

        UserForm form = new UserForm();
        form.setRole("STAFF");
        form.setHotelId(sessionData.hotelId());

        model.addAttribute("activePage", "users");
        model.addAttribute("form", form);
        model.addAttribute("userId", null);

        return "user-form";
    }

    @PostMapping("/users")
    public String addUser(@Valid @ModelAttribute("form") UserForm form,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "users");
        model.addAttribute("userId", null);

        if (form.getPassword() == null || form.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 8 characters");
        }

        if (bindingResult.hasErrors()) {
            return "user-form";
        }

        try {
            UserResponse user = userService.save(sessionData, form);
            redirectAttributes.addFlashAttribute("success", "User " + user.username() + " created.");
            return "redirect:/users";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "user-form";
        }
    }

    @GetMapping("/users/{userId}/edit")
    public String editUser(@PathVariable int userId,
                           HttpSession session,
                           Model model) {

        SessionData sessionData = sessionService.get(session);

        UserResponse user = userService.findById(sessionData, userId);

        UserForm form = new UserForm();
        form.setUsername(user.username());
        form.setEmail(user.email());
        form.setRole(user.role());
        form.setHotelId(user.hotelId());
        form.setEnabled(user.enabled());

        model.addAttribute("activePage", "users");
        model.addAttribute("form", form);
        model.addAttribute("userId", userId);

        return "user-form";
    }

    @PostMapping("/users/{userId}")
    public String updateUser(@PathVariable int userId,
                             @Valid @ModelAttribute("form") UserForm form,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        SessionData sessionData = sessionService.get(session);

        model.addAttribute("activePage", "users");
        model.addAttribute("userId", userId);

        // an empty password field keeps the old password
        if (form.getPassword() != null && !form.getPassword().isEmpty() && form.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 8 characters");
        }

        if (bindingResult.hasErrors()) {
            return "user-form";
        }

        if (form.getPassword() != null && form.getPassword().isEmpty()) {
            form.setPassword(null);
        }

        try {
            UserResponse user = userService.update(sessionData, userId, form);
            redirectAttributes.addFlashAttribute("success", "User " + user.username() + " updated.");
            return "redirect:/users";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "user-form";
        }
    }
}
