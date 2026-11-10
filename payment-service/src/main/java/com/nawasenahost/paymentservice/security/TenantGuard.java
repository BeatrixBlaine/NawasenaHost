package com.nawasenahost.paymentservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Multi-tenant guard.
 * - Hotel staff tokens (HS256, carry a "role" claim) may only touch their own hotel.
 * - Service tokens (RS256, client credentials, no "role" claim) are trusted and bypass the check,
 *   because the user-facing service that received the original request already checked the tenant.
 */
@Component
public class TenantGuard {

    public void assertHotel(int hotelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AccessDeniedException("Access denied");
        }

        Jwt jwt = jwtAuthentication.getToken();

        // service-to-service call (client credentials), no user behind it
        if (jwt.getClaimAsString("role") == null) {
            return;
        }

        Object tokenHotelId = jwt.getClaim("hotelId");

        if (!(tokenHotelId instanceof Number number) || number.intValue() != hotelId) {
            throw new AccessDeniedException("You are not allowed to access resources of hotel with Id: " + hotelId);
        }
    }

    public boolean isServiceCall() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication instanceof JwtAuthenticationToken jwtAuthentication
                && jwtAuthentication.getToken().getClaimAsString("role") == null;
    }

    public int currentHotelId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication
                && jwtAuthentication.getToken().getClaim("hotelId") instanceof Number number) {
            return number.intValue();
        }

        throw new AccessDeniedException("Access denied");
    }
}
