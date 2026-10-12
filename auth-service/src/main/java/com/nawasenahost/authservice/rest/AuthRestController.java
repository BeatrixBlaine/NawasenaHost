package com.nawasenahost.authservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRestController {

    @GetMapping("/test")
    public String helloWorld() {
        return "Hello world";
    }

}
