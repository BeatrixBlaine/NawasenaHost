package com.nawasenahost.authservice.rest;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/login")
    public String helloWorld() {
        return "Hello world";
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
