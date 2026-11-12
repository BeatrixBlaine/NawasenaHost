package com.nawasenahost.paymentservice.repository;

import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByBookingId(int bookingId);

    List<Payment> findByHotelIdOrderByPaymentidDesc(int hotelId);

    List<Payment> findByHotelIdAndPaymentStatusAndPaidAtBetween(
            int hotelId, PaymentStatus paymentStatus, LocalDateTime from, LocalDateTime to);

    List<Payment> findByHotelIdAndPaymentStatusAndRefundedAtBetween(
            int hotelId, PaymentStatus paymentStatus, LocalDateTime from, LocalDateTime to);

    long countByHotelIdAndPaymentStatus(int hotelId, PaymentStatus paymentStatus);

}
