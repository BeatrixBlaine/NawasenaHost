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
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @Autowired
    public AuthServiceImpl(AuthRepository authRepository,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           CurrentUserService currentUserService,
                           AuditLogService auditLogService) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.auditLogService = auditLogService;
    }

    // ADMIN manages every account, MANAGER only the staff of the own hotel
    private Auth getCurrentAdminOrManager() {
        Auth currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != AuthRole.ADMIN
                && currentUser.getRole() != AuthRole.MANAGER) {
            throw new AccessDeniedException(
                    "Only admins and managers can manage accounts"
            );
        }

        return currentUser;
    }

    // manager can only touch STAFF accounts of the same hotel
    private void assertCanManage(Auth currentUser, Auth targetUser) {

        if (currentUser.getRole() == AuthRole.ADMIN) {
            return;
        }

        if (targetUser.getHotelId() != currentUser.getHotelId()
                || targetUser.getRole() != AuthRole.STAFF) {
            throw new UserNotFoundException("User not found");
        }
    }

    private void audit(Auth actor, String action, Auth target, String details) {
        auditLogService.record(
                actor.getUsername(),
                actor.getRole().name(),
                target.getHotelId(),
                action,
                "USER",
                String.valueOf(target.getAuthId()),
                details);
    }

    @Override
    public List<Auth> findAll(Integer hotelId) {
        Auth currentUser = getCurrentAdminOrManager();

        if (currentUser.getRole() == AuthRole.ADMIN) {
            return hotelId == null
                    ? authRepository.findAll()
                    : authRepository.findAllByHotelId(hotelId);
        }

        return authRepository.findAllByHotelId(currentUser.getHotelId());
    }

    @Override
    public Auth findById(int id) {

        Auth currentUser = getCurrentAdminOrManager();

        Auth targetUser = authRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        assertCanManage(currentUser, targetUser);

        return targetUser;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authenticatedUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));

        Auth theAuth = authRepository.findByUsername(authenticatedUser.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generateToken(authenticatedUser.getName(), theAuth.getRole().name(), theAuth.getHotelId()));
        response.setAuthId(theAuth.getAuthId());
        response.setHotelId(theAuth.getHotelId());
        response.setTokenType("Bearer");

        audit(theAuth, "LOGIN", theAuth, "Signed in");

        return response;
    }

    @Override
    public void deleteById(int id) {
        Auth currentUser = getCurrentAdminOrManager();

        Auth tempAuth = findById(id);

        if (tempAuth.getAuthId().equals(currentUser.getAuthId())) {
            throw new AccessDeniedException(
                    "You cannot delete your own account"
            );
        }

        authRepository.deleteById(id);

        audit(currentUser, "USER_DELETED", tempAuth, "Deleted account " + tempAuth.getUsername());
    }

    @Override
    public Auth save(AuthRequest authRequest) {

        Auth currentUser = getCurrentAdminOrManager();

        int hotelId;

        if (currentUser.getRole() == AuthRole.MANAGER) {
            // managers only create staff for their own hotel
            if (authRequest.getRole() != AuthRole.STAFF) {
                throw new AccessDeniedException(
                        "Managers can only create STAFF accounts"
                );
            }

            hotelId = currentUser.getHotelId();
        } else if (authRequest.getRole() == AuthRole.ADMIN) {
            hotelId = 0;
        } else {
            if (authRequest.getHotelId() == null || authRequest.getHotelId() < 1) {
                throw new IllegalArgumentException("Hotel ID is required for this role");
            }

            hotelId = authRequest.getHotelId();
        }

        if (authRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Auth auth = new Auth();

        auth.setHotelId(hotelId);
        auth.setUsername(authRequest.getUsername());
        auth.setEmail(authRequest.getEmail());
        auth.setPassword(
                passwordEncoder.encode(authRequest.getPassword())
        );
        auth.setRole(authRequest.getRole());

        Auth savedAuth = authRepository.save(auth);

        audit(currentUser, "USER_CREATED", savedAuth,
                "Created " + savedAuth.getRole() + " account " + savedAuth.getUsername());

        return savedAuth;
    }

    @Override
    public Auth update(int authId, AuthUpdateRequest request) {
        Auth currentUser = getCurrentAdminOrManager();

        Auth targetUser = authRepository.findById(authId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        assertCanManage(currentUser, targetUser);

        if (authRepository.findByUsername(request.getUsername())
                .filter(existing -> !existing.getAuthId().equals(authId))
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

        if (request.getEnabled() != null) {

            if (!request.getEnabled() && targetUser.getAuthId().equals(currentUser.getAuthId())) {
                throw new AccessDeniedException("You cannot disable your own account");
            }

            targetUser.setEnabled(request.getEnabled());
        }

        Auth savedAuth = authRepository.save(targetUser);

        audit(currentUser, "USER_UPDATED", savedAuth, "Updated account " + savedAuth.getUsername());

        return savedAuth;
    }

}
