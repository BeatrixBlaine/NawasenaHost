package com.nawasenahost.adminpanel.dto;

public record CustomerResponse(int customerId,
                               String firstName,
                               String lastName,
                               String email,
                               String phone) {
}
