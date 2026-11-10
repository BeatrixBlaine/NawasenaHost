package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.entity.Availability;
import com.nawasenahost.availabilityservice.entity.RoomInventory;

import java.util.List;

public interface RoomInventoryService {

    List<RoomInventory> findAll();
    RoomInventory findById(int id);
    RoomInventory save(RoomInventory RoomInventory);
    void deleteById(int id);
    RoomInventory update(int id, RoomInventory roomInventory);
    void initializeInventory(int hotelId, int totalRooms);

}
