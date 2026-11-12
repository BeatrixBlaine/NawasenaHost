package com.nawasenahost.adminpanel.dto;

import java.time.LocalDateTime;

public record UserResponse(int authId,
                           int hotelId,
                           String username,
                           String email,
                           String role,
                           boolean enabled,
                           LocalDateTime createdAt) {
}
