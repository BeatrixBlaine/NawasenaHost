package com.nawasenahost.availabilityservice.repository;

import com.nawasenahost.availabilityservice.entity.Availability;
import com.nawasenahost.availabilityservice.entity.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
    List<Availability> findByRoomIdAndStatusInAndDateGreaterThanEqualAndDateLessThan(
            int roomId,
            List<AvailabilityStatus> booked,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    List<Availability> findByRoomIdAndDateGreaterThanEqualAndDateLessThan(
            int roomId,
            LocalDate startDate,
            LocalDate endDate);

    List<Availability> findByRoomIdInAndStatusInAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
            List<Integer> roomIds,
            List<AvailabilityStatus> statuses,
            LocalDate startDate,
            LocalDate endDate);

}
