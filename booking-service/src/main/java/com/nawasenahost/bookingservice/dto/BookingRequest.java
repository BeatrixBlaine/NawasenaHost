package com.nawasenahost.bookingservice.dto;

import com.nawasenahost.bookingservice.entity.BookingStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingRequest {

    @Min(value = 1, message = "User ID must be greater than 0")
    private int userId;

    @Min(value = 1, message = "Hotel ID must be greater than 0")
    private int hotelId;

    @Min(value = 1, message = "Room ID must be greater than 0")
    private int roomId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Guest count must be at least 1")
    private int guestCount;

    public @Min(value = 1, message = "User ID must be greater than 0") int getUserId() {
        return userId;
    }

    public void setUserId(@Min(value = 1, message = "User ID must be greater than 0") int userId) {
        this.userId = userId;
    }

    public @Min(value = 1, message = "Hotel ID must be greater than 0") int getHotelId() {
        return hotelId;
    }

    public void setHotelId(@Min(value = 1, message = "Hotel ID must be greater than 0") int hotelId) {
        this.hotelId = hotelId;
    }

    public @Min(value = 1, message = "Room ID must be greater than 0") int getRoomId() {
        return roomId;
    }

    public void setRoomId(@Min(value = 1, message = "Room ID must be greater than 0") int roomId) {
        this.roomId = roomId;
    }

    public @NotNull(message = "Check-in date is required") @Future(message = "Check-in date must be in the future") LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(@NotNull(message = "Check-in date is required") @Future(message = "Check-in date must be in the future") LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public @NotNull(message = "Check-out date is required") @Future(message = "Check-out date must be in the future") LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(@NotNull(message = "Check-out date is required") @Future(message = "Check-out date must be in the future") LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public @Min(value = 1, message = "Guest count must be at least 1") int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(@Min(value = 1, message = "Guest count must be at least 1") int guestCount) {
        this.guestCount = guestCount;
    }
}
