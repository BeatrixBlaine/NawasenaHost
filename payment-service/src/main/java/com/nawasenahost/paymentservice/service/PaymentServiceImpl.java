package com.nawasenahost.paymentservice.service;

import com.nawasenahost.paymentservice.client.BookingClient;
import com.nawasenahost.paymentservice.client.RoomClient;
import com.nawasenahost.paymentservice.dto.BookingResponse;
import com.nawasenahost.paymentservice.audit.AuditPublisher;
import com.nawasenahost.paymentservice.dto.DailyAmount;
import com.nawasenahost.paymentservice.dto.PaymentReport;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final RoomClient roomClient;
    private final TenantGuard tenantGuard;
    private final AuditPublisher auditPublisher;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingClient bookingClient,
                              RoomClient roomClient,
                              TenantGuard tenantGuard,
                              AuditPublisher auditPublisher) {
        this.paymentRepository = paymentRepository;
        this.bookingClient = bookingClient;
        this.roomClient = roomClient;
        this.tenantGuard = tenantGuard;
        this.auditPublisher = auditPublisher;
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

        BookingResponse bookingResponse = validateBooking(paymentRequest.getBookingId());

        Payment tempPayment = new Payment();
        tempPayment.setBookingId(paymentRequest.getBookingId());
        tempPayment.setHotelId(bookingResponse.getHotelId());
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

        Payment savedPayment = paymentRepository.save(confirmedPayment);

        auditPublisher.publish(
                savedPayment.getHotelId(),
                "PAYMENT_CONFIRMED",
                "PAYMENT",
                savedPayment.getPaymentid(),
                "Payment of " + savedPayment.getAmount() + " confirmed via " + paymentMethod + " / " + paymentSource);

        return savedPayment;
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

    // repeating the call is safe: SUCCESS becomes REFUNDED, PENDING becomes CANCELLED, anything else is returned as it is
    @Override
    public Payment refundPaymentByBookingId(int bookingId) {

        Payment refundedPayment = paymentRepository.findByBookingId(bookingId);

        if (refundedPayment == null) {
            throw new PaymentNotFoundException("Payment for booking with Id: " + bookingId + " not found");
        }

        if (refundedPayment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            refundedPayment.setPaymentStatus(PaymentStatus.REFUNDED);
            refundedPayment.setRefundedAt(LocalDateTime.now());
        } else if (refundedPayment.getPaymentStatus() == PaymentStatus.PENDING) {
            refundedPayment.setPaymentStatus(PaymentStatus.CANCELLED);
        } else {
            return refundedPayment;
        }

        refundedPayment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(refundedPayment);

        auditPublisher.publish(
                savedPayment.getHotelId(),
                "PAYMENT_" + savedPayment.getPaymentStatus(),
                "PAYMENT",
                savedPayment.getPaymentid(),
                "Payment of booking " + bookingId + " is now " + savedPayment.getPaymentStatus());

        return savedPayment;
    }

    @Override
    public List<Payment> findByHotelId(int hotelId) {

        tenantGuard.assertHotel(hotelId);

        return paymentRepository.findByHotelIdOrderByPaymentidDesc(hotelId);
    }

    @Override
    public PaymentReport getReport(int hotelId, LocalDate from, LocalDate to) {

        tenantGuard.assertHotel(hotelId);

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        // revenue = payments that are SUCCESS, a refund leaves the revenue of its day
        List<Payment> paid = paymentRepository
                .findByHotelIdAndPaymentStatusAndPaidAtBetween(hotelId, PaymentStatus.SUCCESS, start, end);
        List<Payment> refunded = paymentRepository
                .findByHotelIdAndPaymentStatusAndRefundedAtBetween(hotelId, PaymentStatus.REFUNDED, start, end);

        BigDecimal totalRevenue = paid.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundedAmount = refunded.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byMethod = new TreeMap<>();
        Map<String, BigDecimal> bySource = new TreeMap<>();
        Map<LocalDate, BigDecimal> perDay = new TreeMap<>();

        // one entry for every day, also the empty ones, so a chart has no holes
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            perDay.put(date, BigDecimal.ZERO);
        }

        for (Payment payment : paid) {
            String method = payment.getPaymentMethod() == null ? "UNKNOWN" : payment.getPaymentMethod().name();
            String source = payment.getPaymentSource() == null ? "UNKNOWN" : payment.getPaymentSource().name();

            byMethod.merge(method, payment.getAmount(), BigDecimal::add);
            bySource.merge(source, payment.getAmount(), BigDecimal::add);
            perDay.merge(payment.getPaidAt().toLocalDate(), payment.getAmount(), BigDecimal::add);
        }

        PaymentReport report = new PaymentReport();
        report.setFrom(from);
        report.setTo(to);
        report.setTotalRevenue(totalRevenue);
        report.setRefundedAmount(refundedAmount);
        report.setSuccessCount(paid.size());
        report.setRefundedCount(refunded.size());
        report.setPendingCount(paymentRepository.countByHotelIdAndPaymentStatus(hotelId, PaymentStatus.PENDING));
        report.setAveragePayment(paid.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(paid.size()), 2, RoundingMode.HALF_UP));
        report.setRevenueByMethod(byMethod);
        report.setRevenueBySource(bySource);
        report.setDailyRevenue(perDay.entrySet().stream()
                .map(entry -> new DailyAmount(entry.getKey(), entry.getValue()))
                .toList());

        return report;
    }

    // payments created before hotelId existed get the hotel of their booking
    @Override
    public int backfillHotelId() {

        int updated = 0;

        for (Payment payment : paymentRepository.findByHotelIdOrderByPaymentidDesc(0)) {
            try {
                payment.setHotelId(validateBooking(payment.getBookingId()).getHotelId());
                paymentRepository.save(payment);
                updated++;
            } catch (BookingNotFoundException e) {
                // the booking is gone, nothing to copy
            }
        }

        return updated;
    }
}
