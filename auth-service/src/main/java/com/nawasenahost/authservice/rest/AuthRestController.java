package com.nawasenahost.authservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    @GetMapping("/auth/login")
    public String helloWorld() {
        return "Hello world";
    }

}
