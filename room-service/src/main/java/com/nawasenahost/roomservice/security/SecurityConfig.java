package com.nawasenahost.roomservice.security;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        JwtDecoder managerJwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtDecoder serviceJwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwksUri)
                .build();

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
                .csrf(configurer -> configurer.disable())
                .formLogin(configurer -> configurer.disable())
                .httpBasic(configurer -> configurer.disable())
                .authorizeHttpRequests(configurer -> configurer

                        .requestMatchers(HttpMethod.GET,"/api/rooms/single-room/{roomId}").hasAuthority("SCOPE_room.read")
                        .requestMatchers(HttpMethod.GET,"/test/hotelcalls").permitAll()
                        .requestMatchers(
                                "/my-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/my-api-docs/**").permitAll()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(configurer -> configurer
                        .jwt(Customizer.withDefaults()))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing or invalid access token\"}");
                        }));

        return http.build();
    }

}
