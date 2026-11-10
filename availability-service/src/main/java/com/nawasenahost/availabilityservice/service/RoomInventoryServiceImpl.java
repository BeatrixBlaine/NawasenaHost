package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.client.RoomClient;
import com.nawasenahost.availabilityservice.entity.RoomInventory;
import com.nawasenahost.availabilityservice.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomInventoryServiceImpl implements RoomInventoryService{

    private final RoomInventoryRepository roomInventoryRepository;
    private final RoomClient roomClient;

    public RoomInventoryServiceImpl(RoomInventoryRepository roomInventoryRepository, RoomClient roomClient) {
        this.roomInventoryRepository = roomInventoryRepository;
        this.roomClient = roomClient;
    }

    @Override
    @Transactional
    public void initializeInventory(int hotelId, int totalRooms) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(12);

        // load existing inventory once instead of querying every day
        Map<LocalDate, RoomInventory> existingInventory = new HashMap<>();

        for (RoomInventory theInventory : roomInventoryRepository
                .findByHotelIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        hotelId, startDate, endDate)) {
            existingInventory.put(theInventory.getDate(), theInventory);
        }

        List<RoomInventory> inventories = new ArrayList<>();

        for (LocalDate date = startDate;
             date.isBefore(endDate);
             date = date.plusDays(1)) {

            RoomInventory tempInventory = existingInventory.get(date);

            if (tempInventory == null) {
                tempInventory = new RoomInventory(hotelId, date, 0, 0, 0);
            }

            // total follows the real room count, so calling this twice gives the same result
            tempInventory.setTotal(totalRooms);
            tempInventory.setAvailable(Math.max(totalRooms - tempInventory.getReserved(), 0));

            inventories.add(tempInventory);
        }

        roomInventoryRepository.saveAll(inventories);
    }

    @Override
    public List<RoomInventory> findAll() {
        return List.of();
    }

    @Override
    public RoomInventory findById(int id) {
        return null;
    }

    @Override
    public RoomInventory save(RoomInventory RoomInventory) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public RoomInventory update(int id, RoomInventory roomInventory) {
        return null;
    }
}
