package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.AuthRequest;
import com.nawasenahost.authservice.dto.AuthUpdateRequest;
import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import com.nawasenahost.authservice.entity.Auth;

import java.util.List;

public interface AuthService {

    List<Auth> findAll();
    Auth findById(int id);
    LoginResponse login (LoginRequest loginRequest);
    void deleteById(int id);
    Auth update(int authId, AuthUpdateRequest authUpdateRequest);
    Auth save(AuthRequest authRequest);

}
