package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.LoginRequest;
import com.nawasenahost.adminpanel.dto.LoginResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthServiceImpl implements AuthService {

    private final WebClient webClient;
    private final JwtDecoder jwtDecoder;

    public AuthServiceImpl(WebClient webClient,
                           JwtDecoder jwtDecoder) {
        this.webClient = webClient;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public SessionData login(String username, String password) {

        try {
            LoginResponse response = webClient.post()
                    .uri("/api/auth/login")
                    .bodyValue(new LoginRequest(username, password))
                    .retrieve()
                    .bodyToMono(LoginResponse.class)
                    .block();

            // JwtException is thrown when the token cannot be verified
            Jwt jwt = jwtDecoder.decode(response.getToken());

            return new SessionData(
                    response.getToken(),
                    jwt.getClaimAsString("role"),
                    response.getAuthId(),
                    response.getHotelId(),
                    response.getTokenType(),
                    username,
                    null
            );
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
