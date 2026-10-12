package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

}
