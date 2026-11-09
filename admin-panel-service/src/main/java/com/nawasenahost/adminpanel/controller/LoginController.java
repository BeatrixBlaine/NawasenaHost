package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.exception.ErrorResponse;
import com.nawasenahost.adminpanel.dto.LoginRequest;
import com.nawasenahost.adminpanel.dto.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
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
    private final JwtDecoder jwtDecoder;

    public LoginController(WebClient webClient,
                           JwtDecoder jwtDecoder) {
        this.webClient = webClient;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/")
    public String defaultPage() {
        return "redirect:/dashboard";
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
             session.setAttribute("authId", response.getAuthId());
             session.setAttribute("hotelId", response.getHotelId());
             session.setAttribute("tokenType", response.getTokenType());

             Jwt jwt = jwtDecoder.decode(response.getToken());

             String authenticatedUsername = jwt.getSubject();
             String role = jwt.getClaimAsString("role");

             var authentication =
                     new UsernamePasswordAuthenticationToken(
                            authenticatedUsername,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                            )
                    );

             SecurityContext context = SecurityContextHolder.createEmptyContext();
             context.setAuthentication(authentication);
             SecurityContextHolder.setContext(context);
             session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
             session.setAttribute("role", role);

             return "redirect:/dashboard";

        } catch (WebClientResponseException e) {

            ErrorResponse error;

            try {
                error = e.getResponseBodyAs(ErrorResponse.class);
            } catch (Exception ignored) {
                error = null;
            }

            if (error == null) {
                error = new ErrorResponse(
                        e.getStatusCode().value(),
                        "An unexpected error occurred.",
                        System.currentTimeMillis()
                );
            }

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return "redirect:/login-page";
            }

            model.addAttribute("error", error);

            return "login";

        } catch (JwtException e) {
            model.addAttribute(
                    "error",
                    "Invalid or expired authentication token. Please log in again."
            );

            return "login";
        }

    }


}
