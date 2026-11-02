package com.nawasenahost.hotelservice.security;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.secret}") String secret,
            @Value("${auth.jwks-uri}") String jwksUri) {

        // Existing manager JWT decoder (HS256)
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        JwtDecoder managerJwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // New service-to-service JWT decoder (RS256)
        JwtDecoder serviceJwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwksUri)
                .build();

        // Select decoder based on token signing algorithm
        return token -> {
            try {
                String algorithm = SignedJWT.parse(token)
                        .getHeader()
                        .getAlgorithm()
                        .getName();

                if ("RS256".equals(algorithm)) {
                    return serviceJwtDecoder.decode(token);
                }

                if ("HS256".equals(algorithm)) {
                    return managerJwtDecoder.decode(token);
                }

                throw new JwtException("Unsupported JWT signing algorithm");

            } catch (ParseException exception) {
                throw new JwtException("Invalid JWT format", exception);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults()))
                .httpBasic(hlc -> hlc.disable())
                .formLogin(flc -> flc.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(configurer ->
                        configurer
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing or invalid access token\"}");
                }))
        );

        return http.build();
    }

}
