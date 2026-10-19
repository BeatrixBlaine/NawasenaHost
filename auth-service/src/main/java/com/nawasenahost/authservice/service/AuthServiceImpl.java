package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.AuthRequest;
import com.nawasenahost.authservice.dto.AuthUpdateRequest;
import com.nawasenahost.authservice.dto.LoginRequest;
import com.nawasenahost.authservice.dto.LoginResponse;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.exception.UserNotFoundException;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService{

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthRepository authRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    private Auth getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required");
        }

        return authRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Authenticated account not found"));
    }

    private Auth getCurrentManager() {
        Auth currentUser = getCurrentUser();

        if (currentUser.getRole() != AuthRole.MANAGER) {
            throw new AccessDeniedException(
                    "Only managers can manage staff accounts"
            );
        }

        return currentUser;
    }

    @Override
    public List<Auth> findAll() {
        Auth currentManager = getCurrentManager();

        return authRepository.findAllByHotelId(currentManager.getHotelId());
    }

    @Override
    public Auth findById(int id) {

        Auth currentManager = getCurrentManager();

        Auth targetUser = authRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (targetUser.getHotelId() != currentManager.getHotelId()) {
            throw new UserNotFoundException("User not found");
        }

        return targetUser;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authenticatedUser;

        try {
            authenticatedUser = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw e;
        }

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

    @Override
    public void deleteById(int id) {
        findById(id);
        authRepository.deleteById(id);
    }

    @Override
    public Auth save(AuthRequest authRequest) {

        Auth currentManager = getCurrentManager();

        if (authRequest.getRole() != AuthRole.ADMIN) {
            throw new AccessDeniedException(
                    "Managers can only create ADMIN accounts"
            );
        }

        if (authRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }


        Auth auth = new Auth();

        auth.setHotelId(currentManager.getHotelId());
        auth.setUsername(authRequest.getUsername());
        auth.setEmail(authRequest.getEmail());
        auth.setPassword(
                passwordEncoder.encode(authRequest.getPassword())
        );
        auth.setRole(authRequest.getRole());

        return authRepository.save(auth);
    }

    @Override
    public Auth update(int authId, AuthUpdateRequest request) {
        Auth currentManager = getCurrentManager();

        Auth targetUser = authRepository.findById(authId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (targetUser.getHotelId() != currentManager.getHotelId()) {
            throw new UserNotFoundException("User not found");
        }

        if (authRepository.findByUsername(request.getUsername())
                .filter(existing -> existing.getAuthId() != authId)
                .isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        targetUser.setUsername(request.getUsername());
        targetUser.setEmail(request.getEmail());

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            targetUser.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        return authRepository.save(targetUser);
    }

}