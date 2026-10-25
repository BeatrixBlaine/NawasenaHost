package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.LoginRequest;
import com.nawasenahost.adminpanel.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

@Controller
public class LoginController {

    private final WebClient webClient;

    public LoginController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/")
    public String defaultPage() {
        return "redirect:/home";
    }

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        try {

             LoginResponse response = webClient.post()
                     .uri("/api/auth/login")
                     .bodyValue(
                             new LoginRequest(username, password)
                     )
                     .retrieve()
                     .bodyToMono(LoginResponse.class)
                     .block();

             session.setAttribute("accessToken", response.getToken());
             session.setAttribute("role", response.getRole());
             session.setAttribute("authId", response.getAuthId());
             session.setAttribute("hotelId", response.getHotelId());
             session.setAttribute("tokenType", response.getTokenType());

             var authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + response.getRole()
                                    )
                            )
                    );

             SecurityContext context = SecurityContextHolder.createEmptyContext();

             context.setAuthentication(authentication);

             SecurityContextHolder.setContext(context);

             session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

             return "redirect:/dashboard";

        } catch (WebClientResponseException e) {

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                model.addAttribute(
                        "error",
                        "Invalid username or password"
                );
            } else {
                model.addAttribute(
                        "error",
                        "Login service error. Please try again later."
                );
            }

            return "login";

        }

    }

}
