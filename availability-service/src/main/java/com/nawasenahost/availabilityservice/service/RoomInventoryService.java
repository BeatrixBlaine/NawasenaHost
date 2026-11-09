package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.entity.RoomInventory;

import java.time.LocalDate;
import java.util.List;

public interface RoomInventoryService {

    List<RoomInventory> findByHotelIdAndDateRange(int hotelId, LocalDate startDate, LocalDate endDate);
    void initializeInventory(int hotelId, int totalRooms);

}
