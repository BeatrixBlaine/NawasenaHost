package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private final AuthRepository authRepository;

    public CurrentUserServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public Auth getCurrentUser() {
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

    @Override
    public AuthRole getRole() {
        return getCurrentUser().getRole();
    }

    @Override
    public int getHotelId() {
        return getCurrentUser().getHotelId();
    }
}
