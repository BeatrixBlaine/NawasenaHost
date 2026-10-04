package com.nawasenahost.availabilityservice.dto;

import com.nawasenahost.availabilityservice.entity.AvailabilityStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AvailabilityRequest {

    @Min(value = 1, message = "Hotel ID must be greater than 0")
    private int roomId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private AvailabilityStatus status;

    public @Min(value = 1, message = "Hotel ID must be greater than 0") int getRoomId() {
        return roomId;
    }

    public void setRoomId(@Min(value = 1, message = "Hotel ID must be greater than 0") int roomId) {
        this.roomId = roomId;
    }

    public @NotNull(message = "Date is required") LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull(message = "Date is required") LocalDate date) {
        this.date = date;
    }

    public @NotNull(message = "Status is required") AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") AvailabilityStatus status) {
        this.status = status;
    }
}
