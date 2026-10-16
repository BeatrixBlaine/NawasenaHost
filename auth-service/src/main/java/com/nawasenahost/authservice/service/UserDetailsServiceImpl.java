package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // executed when .authenticate is used
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Auth tempAuth = authRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(tempAuth.getUsername())
                .password(tempAuth.getPassword())
                .roles(tempAuth.getRole().name())
                .disabled(!tempAuth.isEnabled())
                .build();
    }

}