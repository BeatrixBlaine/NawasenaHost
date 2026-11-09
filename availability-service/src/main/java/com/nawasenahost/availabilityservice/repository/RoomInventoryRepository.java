package com.nawasenahost.availabilityservice.repository;

import com.nawasenahost.availabilityservice.entity.RoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Integer> {

    List<RoomInventory>
    findByHotelIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
            int hotelId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<RoomInventory> findByHotelIdAndDate(
            int hotelId, LocalDate date);

    List<RoomInventory> findByHotelIdAndDateBetween(
            int hotelId, LocalDate startDate, LocalDate endDate);

}
