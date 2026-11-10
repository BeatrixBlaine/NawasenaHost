package com.nawasenahost.bookingservice.repository;

import com.nawasenahost.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByHotelIdOrderByBookingIdDesc(int hotelId);

}
