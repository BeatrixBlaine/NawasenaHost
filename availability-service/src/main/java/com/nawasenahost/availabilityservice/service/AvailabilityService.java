package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.entity.Availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<Availability> findAll();
    Availability findById(int id);
    Availability save(AvailabilityRequest availabilityRequest);
    void deleteById(int id);
    Availability update(int id, AvailabilityRequest availabilityRequest);
    boolean isAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate);
    List<Availability> createBooking(int roomId, LocalDate checkInDate, LocalDate checkOutDate);

}
