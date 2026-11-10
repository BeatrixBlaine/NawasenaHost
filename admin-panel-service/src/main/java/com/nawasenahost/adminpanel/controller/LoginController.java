package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AuthService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    private final AuthService authService;
    private final SessionService sessionService;
    private final SecurityContextRepository securityContextRepository;

    public LoginController(AuthService authService,
                           SessionService sessionService,
                           SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.securityContextRepository = securityContextRepository;
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
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {

        try {
            SessionData sessionData = authService.login(username, password);

            // new session id after login, against session fixation
            request.getSession(true);
            request.changeSessionId();
            HttpSession session = request.getSession();

            sessionService.save(session, sessionData);

            var authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + sessionData.role()))
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/dashboard";
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
            return "login";
        } catch (JwtException e) {
            model.addAttribute("error", "Invalid or expired authentication token. Please log in again.");
            return "login";
        }
    }
}
