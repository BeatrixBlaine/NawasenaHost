package com.nawasenahost.adminpanel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(hlc -> hlc.disable())
                .csrf(configurer -> configurer.disable());

        return http.build();
    }

}
