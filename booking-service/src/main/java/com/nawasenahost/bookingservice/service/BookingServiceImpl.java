package com.nawasenahost.bookingservice.service;

import com.nawasenahost.bookingservice.client.*;
import com.nawasenahost.bookingservice.audit.AuditPublisher;
import com.nawasenahost.bookingservice.dto.BookingReport;
import com.nawasenahost.bookingservice.dto.BookingRequest;
import com.nawasenahost.bookingservice.dto.DailyCount;
import com.nawasenahost.bookingservice.dto.BookingResponse;
import com.nawasenahost.bookingservice.dto.PaymentResponse;
import com.nawasenahost.bookingservice.entity.Booking;
import com.nawasenahost.bookingservice.entity.BookingStatus;
import com.nawasenahost.bookingservice.exception.*;
import com.nawasenahost.bookingservice.dto.RoomResponse;
import com.nawasenahost.bookingservice.repository.BookingRepository;
import com.nawasenahost.bookingservice.security.TenantGuard;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;
    private final RoomClient roomClient;
    private final AvailabilityClient availabilityClient;
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;
    private final TenantGuard tenantGuard;
    private final AuditPublisher auditPublisher;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              HotelClient hotelClient,
                              RoomClient roomClient,
                              AvailabilityClient availabilityClient,
                              PaymentClient paymentClient,
                              CustomerClient customerClient,
                              TenantGuard tenantGuard,
                              AuditPublisher auditPublisher) {
        this.bookingRepository = bookingRepository;
        this.hotelClient = hotelClient;
        this.roomClient = roomClient;
        this.availabilityClient = availabilityClient;
        this.paymentClient = paymentClient;
        this.customerClient = customerClient;
        this.tenantGuard = tenantGuard;
        this.auditPublisher = auditPublisher;
    }

    // checks if customer exist
    public void validateCustomer(int customerId) {
        try {
            customerClient.getCustomer(customerId);
        } catch (FeignException.NotFound e) {
            throw new CustomerNotFoundException("Customer with Id: " + customerId + " not found");
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

    // checks if room exist and belongs to the hotel of the booking
    public void validateRoom(int roomId, int hotelId) {
        try {
            RoomResponse room = roomClient.getRoom(roomId);

            if (room.getHotelId() != hotelId) {
                throw new RoomNotFoundException(
                        "Room with ID: " + roomId + " does not exist in hotel with ID: " + hotelId
                );
            }
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
    public List<Booking> findByHotelId(int hotelId) {

        tenantGuard.assertHotel(hotelId);

        return bookingRepository.findByHotelIdOrderByBookingIdDesc(hotelId);
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

        tenantGuard.assertHotel(theBooking.getHotelId());

        return theBooking;
    }

    @Override
    public BookingResponse save(BookingRequest bookingRequest) {

        tenantGuard.assertHotel(bookingRequest.getHotelId());

        validateCustomer(bookingRequest.getCustomerId());
        validateDates(bookingRequest);
        validateHotel(bookingRequest.getHotelId());
        validateRoom(bookingRequest.getRoomId(), bookingRequest.getHotelId());
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
        tempBooking.setCustomerId(bookingRequest.getCustomerId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());
        tempBooking.setStatus(BookingStatus.PENDING);
        // tempBooking.setSource(source);
        // tempBooking.setExternalReference(externalReference);
        tempBooking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(tempBooking);

        PaymentResponse paymentResponse = paymentClient.createPayment(savedBooking.getBookingId());

        BookingResponse bookingResponse = new BookingResponse(
                savedBooking.getBookingId(),
                savedBooking.getCustomerId(),
                savedBooking.getHotelId(),
                savedBooking.getRoomId(),
                savedBooking.getStatus().toString(),
                paymentResponse.getPaymentId());

        return bookingResponse;
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        bookingRepository.deleteById(id);
    }

    @Override
    public Booking update(int id, BookingRequest bookingRequest) {

        tenantGuard.assertHotel(bookingRequest.getHotelId());

        validateCustomer(bookingRequest.getCustomerId());
        validateDates(bookingRequest);
        validateHotel(bookingRequest.getHotelId());
        validateRoom(bookingRequest.getRoomId(), bookingRequest.getHotelId());

        Booking tempBooking = findById(id);
        tempBooking.setCustomerId(bookingRequest.getCustomerId());
        tempBooking.setHotelId(bookingRequest.getHotelId());
        tempBooking.setRoomId(bookingRequest.getRoomId());
        tempBooking.setCheckInDate(bookingRequest.getCheckInDate());
        tempBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
        tempBooking.setGuestCount(bookingRequest.getGuestCount());
        tempBooking.setCustomerId(bookingRequest.getCustomerId());

        return bookingRepository.save(tempBooking);
    }

    @Override
    public Booking confirmBooking(int bookingId) {

        Booking tempBooking = findById(bookingId);

        if (tempBooking.getStatus() != BookingStatus.PENDING) {
            throw new BookingCannotBeConfirmedException("Only pending booking can be confirmed");
        }

        // reserve the dates first, so a failed reservation never leaves a CONFIRMED booking behind
        availabilityClient.createBooking(
                tempBooking.getRoomId(),
                tempBooking.getCheckInDate(),
                tempBooking.getCheckOutDate());

        tempBooking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(tempBooking);
    }

    @Override
    public Booking cancelPendingBooking(int bookingId) {

        Booking tempBooking = findById(bookingId);

        if (tempBooking.getStatus() != BookingStatus.PENDING) {
            throw new BookingCannotBeCancelledException("Only pending booking can be cancelled");
        }

        return cancelBooking(bookingId, null);
    }

    // pending booking: only the payment is cancelled, confirmed booking: nights are released and the payment is refunded
    @Override
    public Booking cancelBooking(int bookingId, String reason) {

        Booking tempBooking = findById(bookingId);

        if (tempBooking.getStatus() == BookingStatus.PENDING) {
            paymentClient.cancelPaymentByBookingId(bookingId);
        } else if (tempBooking.getStatus() == BookingStatus.CONFIRMED) {

            // refunding money is not a front desk decision
            tenantGuard.assertManagerOrAdmin();

            if (!tempBooking.getCheckOutDate().isAfter(LocalDate.now())) {
                throw new BookingCannotBeCancelledException("The stay is already finished, it cannot be cancelled");
            }

            // both calls can be repeated safely, so a failed cancel can simply be tried again
            availabilityClient.releaseBooking(
                    tempBooking.getRoomId(),
                    tempBooking.getCheckInDate(),
                    tempBooking.getCheckOutDate());

            paymentClient.refundPaymentByBookingId(bookingId);
        } else {
            throw new BookingCannotBeCancelledException("Only pending or confirmed booking can be cancelled");
        }

        BookingStatus previousStatus = tempBooking.getStatus();

        tempBooking.setStatus(BookingStatus.CANCELLED);
        tempBooking.setCancelledAt(LocalDateTime.now());
        tempBooking.setCancelReason(reason == null || reason.isBlank() ? null : reason);

        Booking cancelledBooking = bookingRepository.save(tempBooking);

        auditPublisher.publish(
                cancelledBooking.getHotelId(),
                "BOOKING_CANCELLED",
                "BOOKING",
                cancelledBooking.getBookingId(),
                "Cancelled " + previousStatus + " booking" + (reason == null || reason.isBlank() ? "" : ": " + reason));

        return cancelledBooking;
    }

    // check-out: the stay has started and the booking is closed
    @Override
    public Booking completeBooking(int bookingId) {

        Booking tempBooking = findById(bookingId);

        if (tempBooking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingCannotBeConfirmedException("Only confirmed booking can be completed");
        }

        if (tempBooking.getCheckInDate().isAfter(LocalDate.now())) {
            throw new BookingCannotBeConfirmedException("The guest has not arrived yet");
        }

        tempBooking.setStatus(BookingStatus.COMPLETED);

        Booking completedBooking = bookingRepository.save(tempBooking);

        auditPublisher.publish(
                completedBooking.getHotelId(),
                "BOOKING_COMPLETED",
                "BOOKING",
                completedBooking.getBookingId(),
                "Guest checked out");

        return completedBooking;
    }

    @Override
    public List<Booking> findByHotelIdAndCustomerId(int hotelId, int customerId) {

        tenantGuard.assertHotel(hotelId);

        return bookingRepository.findByHotelIdAndCustomerIdOrderByBookingIdDesc(hotelId, customerId);
    }

    @Override
    public BookingReport getReport(int hotelId, LocalDate from, LocalDate to) {

        tenantGuard.assertHotel(hotelId);

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }

        List<Booking> bookings = bookingRepository.findByHotelIdAndCheckInDateBetween(hotelId, from, to);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            byStatus.put(status.name(), 0L);
        }
        bookings.forEach(booking -> byStatus.merge(booking.getStatus().name(), 1L, Long::sum));

        Map<String, Long> bySource = bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getSource() == null ? "DIRECT" : booking.getSource(),
                        TreeMap::new,
                        Collectors.counting()));

        // one entry for every day, also the empty ones, so a chart has no holes
        Map<LocalDate, Long> perDay = new TreeMap<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            perDay.put(date, 0L);
        }
        bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .forEach(booking -> perDay.merge(booking.getCheckInDate(), 1L, Long::sum));

        BookingReport report = new BookingReport();
        report.setFrom(from);
        report.setTo(to);
        report.setTotalBookings(bookings.size());
        report.setByStatus(byStatus);
        report.setBySource(bySource);
        report.setDailyCheckIns(perDay.entrySet().stream()
                .map(entry -> new DailyCount(entry.getKey(), entry.getValue()))
                .toList());
        report.setArrivalsToday(bookingRepository.countByHotelIdAndStatusAndCheckInDate(
                hotelId, BookingStatus.CONFIRMED, LocalDate.now()));
        report.setDeparturesToday(bookingRepository.countByHotelIdAndStatusAndCheckOutDate(
                hotelId, BookingStatus.CONFIRMED, LocalDate.now()));

        return report;
    }
}
