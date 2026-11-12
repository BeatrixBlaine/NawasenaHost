package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.dto.HotelResponse;
import com.nawasenahost.adminpanel.service.AuthService;
import com.nawasenahost.adminpanel.service.HotelService;
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
    private final HotelService hotelService;

    public LoginController(AuthService authService,
                           SessionService sessionService,
                           SecurityContextRepository securityContextRepository,
                           HotelService hotelService) {
        this.hotelService = hotelService;
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

            sessionData = withHotelName(sessionData);

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

    // the hotel name is shown in the top bar, an ADMIN starts in the first hotel
    private SessionData withHotelName(SessionData sessionData) {

        try {
            if ("ADMIN".equals(sessionData.role()) && sessionData.hotelId() == 0) {
                List<HotelResponse> hotels = hotelService.findAll(sessionData);

                if (!hotels.isEmpty()) {
                    return sessionData.withHotel(hotels.get(0).id(), hotels.get(0).name());
                }

                return sessionData;
            }

            HotelResponse hotel = hotelService.findById(sessionData, sessionData.hotelId());

            return sessionData.withHotel(hotel.id(), hotel.name());
        } catch (ApiException e) {
            // the name is a nice to have, logging in still works without it
            return sessionData;
        }
    }
}
