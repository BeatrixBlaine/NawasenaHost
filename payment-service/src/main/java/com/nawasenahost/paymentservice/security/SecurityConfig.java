package com.nawasenahost.paymentservice.security;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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

                        .requestMatchers(HttpMethod.GET, "/api/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/payments/hotel/{hotelId}").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/payments/hotel/{hotelId}/report").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/booking/{bookingId}").hasAnyAuthority("SCOPE_payment.read", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/payments/{paymentId}").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/payments/create-payment").hasAuthority("SCOPE_payment.create")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/cancel-payment-by-booking-id").hasAuthority("SCOPE_payment.create")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/refund-by-booking-id").hasAuthority("SCOPE_payment.create")
                        .requestMatchers(HttpMethod.POST, "/api/payments/confirm-payment").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/payments/backfill-hotel-id").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/{paymentId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/payments/{paymentId}").hasRole("ADMIN")
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/my-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/my-api-docs/**").permitAll()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(configurer -> configurer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"You are not allowed to access this resource\"}");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing or invalid access token\"}");
                        }));

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>(scopeConverter.convert(jwt));

            String role = jwt.getClaimAsString("role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            return authorities;
        });

        return converter;
    }

}
