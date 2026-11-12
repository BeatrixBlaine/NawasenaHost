package com.nawasenahost.adminpanel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // same way auth-service signs the token: the secret is Base64 encoded
    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKey key = new SecretKeySpec(
                Base64.getDecoder().decode(jwtSecret),
                "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(
                                "/login-page",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // ADMIN: everything, MANAGER: the assigned hotel, STAFF: front desk
                        // the backend services check the role again, this only hides the pages
                        .requestMatchers("/hotels/**", "/switch-hotel").hasRole("ADMIN")
                        .requestMatchers("/users/**", "/audit-logs/**", "/payments/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/inventory/block", "/inventory/unblock").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/rooms/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/rooms/new", "/rooms/*/edit").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().authenticated()
                )
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository())
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // csrf stays enabled, every th:action form gets its token automatically
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login-page")
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.sendRedirect("/login-page?logout")
                        )
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}
