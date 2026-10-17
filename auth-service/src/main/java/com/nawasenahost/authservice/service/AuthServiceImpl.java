package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.exception.UserNotFoundException;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(AuthRepository authRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public List<Auth> findAll() {
        return authRepository.findAll();
    }

    @Override
    public Auth findById(int id) {

        Optional<Auth> tempAuth = authRepository.findById(id);
        Auth theAuth;

        if(tempAuth.isPresent()) {
            theAuth = tempAuth.get();
        } else {
            throw new UserNotFoundException("User with Id: " + id + " not found");
        }

        return theAuth;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authenticatedUser = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));

        Auth theAuth = authRepository.findByUsername(authenticatedUser.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generateToken(authenticatedUser.getName()));
        response.setAuthId(theAuth.getAuthId());
        response.setHotelId(theAuth.getHotelId());
        response.setRole(theAuth.getRole().name());
        response.setTokenType("Bearer");

        return response;
    }
}
