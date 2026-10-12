package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

}
