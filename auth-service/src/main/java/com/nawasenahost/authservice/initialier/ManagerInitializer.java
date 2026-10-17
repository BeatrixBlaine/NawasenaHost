package com.nawasenahost.authservice.initialier;

import com.nawasenahost.authservice.client.HotelClient;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.exception.HotelNotFoundException;
import com.nawasenahost.authservice.repository.AuthRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ManagerInitializer implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelClient hotelClient;

    @Value("${manager.username}")
    private String username;

    @Value("${manager.password}")
    private String password;

    @Value("${manager.email}")
    private String email;

    @Value("${manager.hotel-id}")
    private int hotelId;

    public ManagerInitializer(AuthRepository authRepository,
                              PasswordEncoder passwordEncoder,
                              HotelClient hotelClient) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.hotelClient = hotelClient;
    }

    public void validateHotel(int hotelId) {
        try {
            hotelClient.getHotel(hotelId);
        } catch (FeignException.NotFound e) {
            throw new HotelNotFoundException("Hotel with Id : " + hotelId + " not found");
        }
    }


    @Override
    public void run(String... args) throws Exception {

        if(authRepository.findByUsername(username).isPresent()) {
            return;
        }

        Auth manager = new Auth();
        manager.setUsername(username);
        manager.setEmail(email);
        manager.setPassword(passwordEncoder.encode(password));
        manager.setEnabled(true);
        manager.setRole(AuthRole.MANAGER);
        manager.setHotelId(hotelId);

        validateHotel(hotelId);

        authRepository.save(manager);
    }
}
