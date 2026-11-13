package com.nawasenahost.availabilityservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantGuard {

    private Jwt currentJwt() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AccessDeniedException("Access denied");
        }

        return jwtAuthentication.getToken();
    }

    public void assertHotel(int hotelId) {

        Jwt jwt = currentJwt();
        String role = jwt.getClaimAsString("role");

        // service-to-service call (client credentials), no user behind it
        if (role == null || "ADMIN".equals(role)) {
            return;
        }

        Object tokenHotelId = jwt.getClaim("hotelId");

        if (!(tokenHotelId instanceof Number number) || number.intValue() != hotelId) {
            throw new AccessDeniedException("You are not allowed to access resources of hotel with Id: " + hotelId);
        }
    }

    public boolean isServiceCall() {
        return currentJwt().getClaimAsString("role") == null;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(currentJwt().getClaimAsString("role"));
    }

    // only manager and admin may continue, staff and anonymous users are refused (service calls pass)
    public void assertManagerOrAdmin() {

        String role = currentJwt().getClaimAsString("role");

        if (role != null && !"ADMIN".equals(role) && !"MANAGER".equals(role)) {
            throw new AccessDeniedException("Only managers and admins can do this");
        }
    }

    // hotel of the current user, ADMIN has no hotel so the requested hotel is used
    public int currentHotelId() {

        Object tokenHotelId = currentJwt().getClaim("hotelId");

        if (tokenHotelId instanceof Number number) {
            return number.intValue();
        }

        throw new AccessDeniedException("Access denied");
    }

    public String currentUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return jwtAuthentication.getToken().getSubject();
        }

        return "unknown";
    }

    public String currentRole() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            String role = jwtAuthentication.getToken().getClaimAsString("role");
            return role == null ? "SERVICE" : role;
        }

        return "UNKNOWN";
    }
}
