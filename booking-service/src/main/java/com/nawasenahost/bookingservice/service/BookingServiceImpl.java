package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.client.AvailabilityClient;
import com.nawasenahost.bookingservice.client.HotelClient;
import com.nawasenahost.bookingservice.client.RoomClient;
import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.entity.BookingStatus;
import com.nawasenahost.bookingservice.exception.BookingNotFoundException;
import com.nawasenahost.bookingservice.exception.HotelNotFoundException;
import com.nawasenahost.bookingservice.exception.RoomNotFoundException;
import com.nawasenahost.bookingservice.repository.BookingRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void validateDates(BookingRequest bookingRequest) {
        if (bookingRequest.getCheckInDate().isAfter(bookingRequest.getCheckOutDate())) {
           throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    public void validateHotel(int hotelId) {
        try {
            hotelClient.getHotel(hotelId);
        } catch(FeignException.NotFound e) {
            throw new HotelNotFoundException("Hotel with ID " + hotelId + " does not exist");
        }
    }

    public void validateRoom(int roomId) {
        try {
            roomClient.getRoom(roomId);
        } catch(FeignException.NotFound e) {
            throw new RoomNotFoundException("Room with ID " + roomId + " does not exist");
        }
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findById(int id) {

        Optional<Booking> tempBooking = bookingRepository.findById(id);

        Booking theBooking;

        if(tempBooking.isPresent()){
            theBooking = tempBooking.get();
        } else {
            throw new BookingNotFoundException("Booking not found with id: " + id);
        }

        return theBooking;
    }

    @Override
    public Booking save(BookingRequest bookingRequest) {

        validateDates(bookingRequest);
        validateHotel(bookingRequest.getHotelId());
        validateRoom(bookingRequest.getRoomId());

        Booking tempBooking = new Booking();
        tempBooking.setUserId(bookingRequest.getUserId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());
        tempBooking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(tempBooking);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        bookingRepository.deleteById(id);
    }

    @Override
    public Booking update(int id, BookingRequest bookingRequest) {

        validateDates(bookingRequest);
        validateHotel(bookingRequest.getHotelId());
        validateRoom(bookingRequest.getRoomId());

        Booking tempBooking = findById(id);
        tempBooking.setUserId(bookingRequest.getUserId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());

        return bookingRepository.save(tempBooking);
    }
}
