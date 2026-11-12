package com.nawasenahost.bookingservice.repository;

import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByHotelIdOrderByBookingIdDesc(int hotelId);

    List<Booking> findByHotelIdAndCustomerIdOrderByBookingIdDesc(int hotelId, int customerId);

    List<Booking> findByHotelIdAndCheckInDateBetween(int hotelId, LocalDate from, LocalDate to);

    long countByHotelIdAndStatusAndCheckInDate(int hotelId, BookingStatus status, LocalDate checkInDate);

    long countByHotelIdAndStatusAndCheckOutDate(int hotelId, BookingStatus status, LocalDate checkOutDate);

    Optional<Booking> findBySourceAndExternalReference(String source, String externalReference);

}
