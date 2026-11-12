package com.nawasenahost.authservice.initialier;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// creates the first system ADMIN, only when the admin credentials are configured
@Component
public class AdminInitializer implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:}")
    private String username;

    @Value("${admin.password:}")
    private String password;

    @Value("${admin.email:}")
    private String email;

    public AdminInitializer(AuthRepository authRepository,
                            PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            return;
        }

        if (authRepository.findByUsername(username).isPresent() || authRepository.existsByEmail(email)) {
            return;
        }

        Auth admin = new Auth();
        admin.setUsername(username);
        admin.setEmail(email.isBlank() ? username + "@nawasenahost.local" : email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEnabled(true);
        admin.setRole(AuthRole.ADMIN);
        admin.setHotelId(0); // ADMIN is not tied to a hotel

        authRepository.save(admin);
    }
}
