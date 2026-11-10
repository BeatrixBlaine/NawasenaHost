package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.dto.RoomResponse;
import com.nawasenahost.availabilityservice.entity.Availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<Availability> findAll();
    Availability findById(int id);
    Availability save(AvailabilityRequest availabilityRequest);
    void deleteById(int id);
    Availability update(int id, AvailabilityRequest availabilityRequest);
    List<RoomResponse> findAvailableRooms(int hotelId, LocalDate checkInDate, LocalDate checkOutDate);
    boolean isAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate);
    List<Availability> createBooking(int roomId, LocalDate checkInDate, LocalDate checkOutDate);

}
