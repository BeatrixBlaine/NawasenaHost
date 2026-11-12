package com.nawasenahost.bookingservice.dto;

import com.nawasenahost.bookingservice.entity.BookingStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingRequest {

    @Min(value = 1, message = "User ID must be greater than 0")
    private int customerId;

    @Min(value = 1, message = "Hotel ID must be greater than 0")
    private int hotelId;

    @Min(value = 1, message = "Room ID must be greater than 0")
    private int roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must not be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @FutureOrPresent(message = "Check-out date must not be in the past")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Guest count must be at least 1")
    private int guestCount;

    // optional, for bookings that come from an OTA later
    @Pattern(regexp = "^[A-Z_]{2,30}$", message = "Source must be uppercase letters, e.g. DIRECT")
    private String source;

    @Size(max = 100, message = "External reference must not exceed 100 characters")
    private String externalReference;

    public @Min(value = 1, message = "User ID must be greater than 0") int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@Min(value = 1, message = "User ID must be greater than 0") int customerId) {
        this.customerId = customerId;
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

    public @NotNull(message = "Check-in date is required") @FutureOrPresent(message = "Check-in date must not be in the past") LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(@NotNull(message = "Check-in date is required") @FutureOrPresent(message = "Check-in date must not be in the past") LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public @NotNull(message = "Check-out date is required") @FutureOrPresent(message = "Check-out date must not be in the past") LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(@NotNull(message = "Check-out date is required") @FutureOrPresent(message = "Check-out date must not be in the past") LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public @Min(value = 1, message = "Guest count must be at least 1") int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(@Min(value = 1, message = "Guest count must be at least 1") int guestCount) {
        this.guestCount = guestCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }
}
