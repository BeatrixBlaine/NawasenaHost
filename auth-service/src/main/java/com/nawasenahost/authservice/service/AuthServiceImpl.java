package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.exception.UserNotFoundException;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
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

        Auth tempAuth = authRepository.findByUsername(loginRequest.getUsername()).
                orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!tempAuth.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), tempAuth.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return null;
    }
}
