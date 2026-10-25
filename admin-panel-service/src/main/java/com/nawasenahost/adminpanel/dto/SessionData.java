package com.nawasenahost.adminpanel.dto;

public record SessionData(
        String token,
        String role,
        int authId,
        int hotelId,
        String tokenType
) {}
