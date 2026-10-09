package com.nawasenahost.bookingservice.repository;

import com.nawasenahost.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
}