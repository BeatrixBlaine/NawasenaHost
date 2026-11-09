package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.entity.RoomInventory;
import com.nawasenahost.availabilityservice.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomInventoryServiceImpl implements RoomInventoryService{

    private final RoomInventoryRepository roomInventoryRepository;

    public RoomInventoryServiceImpl(RoomInventoryRepository roomInventoryRepository) {
        this.roomInventoryRepository = roomInventoryRepository;
    }

    @Override
    public List<RoomInventory> findByHotelIdAndDateRange(int hotelId,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return roomInventoryRepository
                .findByHotelIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        hotelId,
                        startDate,
                        endDate
                );
    }

    @Override
    @Transactional
    public void initializeInventory(int hotelId, int totalRooms) {

        if (totalRooms <= 0) {
            throw new IllegalArgumentException("Total rooms must be greater than 0");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(12);

        for (LocalDate date = startDate;
             date.isBefore(endDate);
             date = date.plusDays(1)) {

            RoomInventory inventory = roomInventoryRepository
                    .findByHotelIdAndDate(hotelId, date)
                    .orElse(new RoomInventory(hotelId, date, 0, 0, 0));

            if (inventory.getReserved() > totalRooms) {
                throw new IllegalStateException(
                        "Reserved rooms exceed total rooms for date: " + date
                );
            }

            inventory.setTotal(totalRooms);
            inventory.setAvailable(totalRooms - inventory.getReserved());

            roomInventoryRepository.save(inventory);
        }
    }
}
