package com.nawasenahost.adminpanel.dto;

import java.time.LocalDateTime;

public record CustomerResponse(int customerId,
                               String firstName,
                               String lastName,
                               String email,
                               String phone,
                               int hotelId,
                               LocalDateTime createdAt) {
}
