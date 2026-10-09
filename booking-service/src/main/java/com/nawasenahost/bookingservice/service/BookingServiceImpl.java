package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.client.*;
import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.entity.BookingStatus;
import com.nawasenahost.bookingservice.exception.*;
import com.nawasenahost.bookingservice.repository.BookingRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;
    private final AvailabilityClient availabilityClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              HotelClient hotelClient,
                              RoomClient roomClient,
                              AvailabilityClient availabilityClient,
                              UserClient userClient,
                              PaymentClient paymentClient) {
        this.bookingRepository = bookingRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
        this.availabilityClient = availabilityClient;
        this.userClient = userClient;
        this.paymentClient = paymentClient;
    }

    // checks if user exist
    public void validateUser(int userId) {
        try {
            userClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User with ID: " + userId + " does not exist");
        }
    }

    // checks if check out date is after check in date
    public void validateDates(BookingRequest bookingRequest) {
        if (!bookingRequest.getCheckOutDate()
                .isAfter(bookingRequest.getCheckInDate())) {

            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date"
            );
        }
    }

    // checks if hotel exist
    public void validateHotel(int hotelId) {
        try {
            hotelClient.getHotel(hotelId);
        } catch(FeignException.NotFound e) {
            throw new HotelNotFoundException("Hotel with ID: " + hotelId + " does not exist");
        }
    }

    // checks if room exist
    public void validateRoom(int roomId) {
        try {
            roomClient.getRoom(roomId);
        } catch(FeignException.NotFound e) {
            throw new RoomNotFoundException("Room with ID: " + roomId + " does not exist");
        }
    }

    // checks if room available
    public boolean validateAvailability(int roomId,
                                        LocalDate checkInDate,
                                        LocalDate checkOutDate) {
        return availabilityClient.checkAvailability(roomId, checkInDate, checkOutDate);
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
        validateUser(bookingRequest.getUserId());
        validateHotel(bookingRequest.getHotelId());
        validateRoom(bookingRequest.getRoomId());
        boolean isAvailable = validateAvailability(
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate());

        if (!isAvailable) {
            throw new RoomNotAvailableException(
                    "Room with ID " + bookingRequest.getRoomId()
                            + " is not available for the selected dates"
            );
        }

        Booking tempBooking = new Booking();
        tempBooking.setUserId(bookingRequest.getUserId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());
        tempBooking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(tempBooking);

        paymentClient.createPayment(savedBooking.getBookingId());

        return savedBooking;
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
        validateUser(bookingRequest.getUserId());

        Booking tempBooking = findById(id);
        tempBooking.setUserId(bookingRequest.getUserId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());

        return bookingRepository.save(tempBooking);
    }

    @Override
    public Booking confirmBooking(int bookingId) {

        Booking tempBooking = findById(bookingId);
        tempBooking.setStatus(BookingStatus.CONFIRMED);

        Booking confirmedBooking = bookingRepository.save(tempBooking);

        availabilityClient.createBooking(
                confirmedBooking.getRoomId(),
                confirmedBooking.getCheckInDate(),
                confirmedBooking.getCheckOutDate());

        return confirmedBooking;
    }
}
