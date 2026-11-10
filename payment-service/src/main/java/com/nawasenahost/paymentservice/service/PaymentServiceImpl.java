package com.nawasenahost.paymentservice.service;

import com.nawasenahost.paymentservice.client.BookingClient;
import com.nawasenahost.paymentservice.client.RoomClient;
import com.nawasenahost.paymentservice.dto.BookingResponse;
import com.nawasenahost.paymentservice.dto.PaymentRequest;
import com.nawasenahost.paymentservice.dto.PaymentResponse;
import com.nawasenahost.paymentservice.dto.RoomResponse;
import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentMethod;
import com.nawasenahost.paymentservice.entity.PaymentSource;
import com.nawasenahost.paymentservice.entity.PaymentStatus;
import com.nawasenahost.paymentservice.exception.BookingNotFoundException;
import com.nawasenahost.paymentservice.exception.PaymentConflictException;
import com.nawasenahost.paymentservice.exception.PaymentNotFoundException;
import com.nawasenahost.paymentservice.repository.PaymentRepository;
import com.nawasenahost.paymentservice.security.TenantGuard;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final RoomClient roomClient;
    private final TenantGuard tenantGuard;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingClient bookingClient,
                              RoomClient roomClient,
                              TenantGuard tenantGuard) {
        this.paymentRepository = paymentRepository;
        this.bookingClient = bookingClient;
        this.roomClient = roomClient;
        this.tenantGuard = tenantGuard;
    }

    // checks if booking exist and belongs to the hotel of the current user
    public BookingResponse validateBooking(int bookingId) {
        try {
            BookingResponse bookingResponse = bookingClient.getBooking(bookingId);

            tenantGuard.assertHotel(bookingResponse.getHotelId());

            return bookingResponse;
        } catch (FeignException.NotFound e) {
            throw new BookingNotFoundException("Booking with Id: " + bookingId + " not found");
        }
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment findById(int id) {

        Optional<Payment> tempPayment = paymentRepository.findById(id);

        Payment thePayment;

        if(tempPayment.isPresent()) {
            thePayment = tempPayment.get();
        } else {
            throw new PaymentNotFoundException("Payment with Id: " + id + " not found");
        }

        validateBooking(thePayment.getBookingId());

        return thePayment;
    }

    @Override
    public Payment findByBookingId(int bookingId) {

        validateBooking(bookingId);

        Payment thePayment = paymentRepository.findByBookingId(bookingId);

        if (thePayment == null) {
            throw new PaymentNotFoundException("Payment for booking with Id: " + bookingId + " not found");
        }

        return thePayment;
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment update(int paymentId, PaymentRequest paymentRequest) {

        validateBooking(paymentRequest.getBookingId());

        Payment tempPayment = findById(paymentId);
        tempPayment.setBookingId(paymentRequest.getBookingId());
        tempPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        tempPayment.setPaymentSource(paymentRequest.getPaymentSource());
        tempPayment.setPaymentStatus(PaymentStatus.PENDING);
        tempPayment.setAmount(paymentRequest.getAmount());
        tempPayment.setCurrency(paymentRequest.getCurrency());
        tempPayment.setProvider(paymentRequest.getProvider());
        tempPayment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(tempPayment);
    }

    @Override
    public Payment save(PaymentRequest paymentRequest) {

        validateBooking(paymentRequest.getBookingId());

        Payment tempPayment = new Payment();
        tempPayment.setBookingId(paymentRequest.getBookingId());
        tempPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        tempPayment.setPaymentSource(paymentRequest.getPaymentSource());
        tempPayment.setPaymentStatus(PaymentStatus.PENDING);
        tempPayment.setAmount(paymentRequest.getAmount());
        tempPayment.setCurrency(paymentRequest.getCurrency());
        tempPayment.setProvider(paymentRequest.getProvider());
        tempPayment.setCreatedAt(LocalDateTime.now());
        tempPayment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(tempPayment);
    }

    @Override
    public Payment confirmPayment(int paymentId, PaymentMethod paymentMethod, PaymentSource paymentSource) {

        // findById already checks the hotel of the booking
        Payment confirmedPayment = findById(paymentId);

        if (confirmedPayment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentConflictException("Only pending payment can be confirmed");
        }

        // confirm the booking first, so a failed reservation never leaves a SUCCESS payment behind
        try {
            bookingClient.confirmBooking(confirmedPayment.getBookingId());
        } catch (FeignException.Conflict e) {
            throw new PaymentConflictException("Booking could not be confirmed, the room is no longer available or the booking is not pending");
        }

        confirmedPayment.setCurrency("IDR");
        confirmedPayment.setPaymentMethod(paymentMethod);
        confirmedPayment.setPaymentSource(paymentSource);
        confirmedPayment.setUpdatedAt(LocalDateTime.now());
        confirmedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        confirmedPayment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(confirmedPayment);
    }

    @Override
    public PaymentResponse createPayment(int bookingId) {

        BookingResponse bookingResponse = validateBooking(bookingId);
        RoomResponse roomResponse = roomClient.getRoom(bookingResponse.getRoomId());

        // Calculate price
        long nights = ChronoUnit.DAYS.between(
                bookingResponse.getCheckInDate(),
                bookingResponse.getCheckOutDate()
        );

        BigDecimal roomPrice = BigDecimal.valueOf(roomResponse.getPrice());
        BigDecimal totalAmount = roomPrice.multiply(BigDecimal.valueOf(nights));

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(bookingId);
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setPaymentMethod(null);
        paymentRequest.setPaymentSource(null);
        paymentRequest.setProvider(null);
        paymentRequest.setCurrency(null);
        paymentRequest.setTransactionId(null);
        Payment savedPayment = save(paymentRequest);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(savedPayment.getPaymentid());

        return paymentResponse;
    }

    @Override
    public Payment cancelPaymentByBookingId(int bookingId) {

        Payment cancelledPayment = paymentRepository.findByBookingId(bookingId);

        if (cancelledPayment == null) {
            throw new PaymentNotFoundException("Payment for booking with Id: " + bookingId + " not found");
        }

        cancelledPayment.setUpdatedAt(LocalDateTime.now());
        cancelledPayment.setPaymentStatus(PaymentStatus.CANCELLED);

        return paymentRepository.save(cancelledPayment);
    }


}
