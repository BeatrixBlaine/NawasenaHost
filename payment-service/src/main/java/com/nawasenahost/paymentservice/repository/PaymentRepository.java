package com.nawasenahost.paymentservice.repository;

import com.nawasenahost.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByBookingId(int bookingId);
}
