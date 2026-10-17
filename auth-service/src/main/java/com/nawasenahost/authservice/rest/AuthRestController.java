package com.nawasenahost.authservice.rest;

import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/auth/users")
    public List<Auth> getUsers() {
        return authService.findAll();
    }

    @GetMapping("/auth/users/{userId}")
    public Auth getUser(@PathVariable int userId) {
        return authService.findById(userId);
    }

}
