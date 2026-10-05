package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.client.AvailabilityClient;
import com.nawasenahost.bookingservice.client.HotelClient;
import com.nawasenahost.bookingservice.client.RoomClient;
import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;
    private final AvailabilityClient availabilityClient;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              HotelClient hotelClient,
                              RoomClient roomClient,
                              AvailabilityClient availabilityClient) {
        this.bookingRepository = bookingRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
        this.availabilityClient = availabilityClient;
    }

    @Override
    public List<Booking> findAll() {
        return List.of();
    }

    @Override
    public Booking findById(int id) {
        return null;
    }

    @Override
    public Booking save(BookingRequest bookingRequest) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public Booking update(int id, BookingRequest bookingRequest) {
        return null;
    }
}
