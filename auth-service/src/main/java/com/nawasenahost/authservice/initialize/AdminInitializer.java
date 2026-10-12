package com.nawasenahost.authservice.initialize;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    public AdminInitializer(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        if (authRepository.findByUsername(username).isPresent() || authRepository.findByEmail(email).isPresent()) {
            return;
        }

        Auth admin = new Auth();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setEnabled(true);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(AuthRole.ADMIN);

        authRepository.save(admin);
    }
}
