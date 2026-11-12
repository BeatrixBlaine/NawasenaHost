package com.nawasenahost.authservice.initialier;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ManagerInitializer implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${manager.username}")
    private String username;

    @Value("${manager.password}")
    private String password;

    @Value("${manager.email}")
    private String email;

    // hotel assigned to the first manager
    @Value("${manager.hotel-id:2}")
    private int hotelId;

    public ManagerInitializer(AuthRepository authRepository,
                              PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            return;
        }

        String managerEmail = email.isBlank()
                ? username + "@nawasenahost.local"
                : email;

        if(authRepository.findByUsername(username).isPresent() || authRepository.existsByEmail(managerEmail)) {
            return;
        }

        Auth manager = new Auth();
        manager.setUsername(username);
        manager.setEmail(email);
        manager.setPassword(passwordEncoder.encode(password));
        manager.setEnabled(true);
        manager.setRole(AuthRole.MANAGER);
        manager.setHotelId(hotelId);

        authRepository.save(manager);
    }
}
