package com.nawasenahost.adminpanel.dto;

public record SessionData(
        String token,
        String role,
        int authId,
        int hotelId,
        String tokenType,
        String username,
        String hotelName
) {

    // an ADMIN works in one hotel at a time, switching hotel gives a new copy of the session
    public SessionData withHotel(int newHotelId, String newHotelName) {
        return new SessionData(token, role, authId, newHotelId, tokenType, username, newHotelName);
    }
}
