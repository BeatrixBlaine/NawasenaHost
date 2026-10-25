package com.nawasenahost.adminpanel.dto;

public record RoomResponse(int roomId,
                           String roomNumber,
                           String roomType,
                           double price,
                           int capacity,
                           String description) {
}
