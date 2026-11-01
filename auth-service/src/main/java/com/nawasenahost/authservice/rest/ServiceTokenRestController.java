package com.nawasenahost.authservice.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceTokenRestController {

    @PostMapping("/oauth2/token")
    public String getToken() {
        return null;
    }

}
