package com.nawasenahost.adminpanel.dto;

public record HotelResponse(int id,
                            String name,
                            String description,
                            String address,
                            String city,
                            String country,
                            String postalCode,
                            String phone,
                            String email) {
}
