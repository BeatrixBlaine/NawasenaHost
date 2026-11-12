package com.nawasenahost.availabilityservice.repository;

import com.nawasenahost.availabilityservice.entity.RoomInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // row lock, two bookings of the same hotel and dates wait for each other instead of overbooking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from RoomInventory i where i.hotelId = :hotelId " +
            "and i.date >= :startDate and i.date < :endDate order by i.date asc")
    List<RoomInventory> lockRange(@Param("hotelId") int hotelId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

}
