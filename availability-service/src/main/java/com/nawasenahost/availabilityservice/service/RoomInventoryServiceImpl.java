package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.client.RoomClient;
import com.nawasenahost.availabilityservice.entity.RoomInventory;
import com.nawasenahost.availabilityservice.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomInventoryServiceImpl implements RoomInventoryService{

    private final RoomInventoryRepository roomInventoryRepository;
    private final RoomClient roomClient;

    public RoomInventoryServiceImpl(RoomInventoryRepository roomInventoryRepository, RoomClient roomClient) {
        this.roomInventoryRepository = roomInventoryRepository;
        this.roomClient = roomClient;
    }

    public void initializeInventory(int hotelId, int totalRooms) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(12);

        for (LocalDate date = startDate;
             date.isBefore(endDate);
             date = date.plusDays(1)) {

            RoomInventory inventory = roomInventoryRepository
                    .findByHotelIdAndDate(hotelId, date)
                    .orElse(new RoomInventory(hotelId, date, 0, 0, 0));

            inventory.setTotal(inventory.getTotal() + totalRooms);

            roomInventoryRepository.save(inventory);
        }
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
