package com.nawasenahost.paymentservice.service;

import com.nawasenahost.paymentservice.dto.PaymentReport;
import com.nawasenahost.paymentservice.dto.PaymentRequest;
import com.nawasenahost.paymentservice.dto.PaymentResponse;
import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentMethod;
import com.nawasenahost.paymentservice.entity.PaymentSource;
import com.nawasenahost.paymentservice.entity.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    List<Payment> findAll();
    Payment findById(int id);
    Payment findByBookingId(int bookingId);
    void deleteById(int id);
    Payment update(int paymentId, PaymentRequest paymentRequest);
    Payment save(PaymentRequest paymentRequest);
    Payment confirmPayment(int paymentId, PaymentMethod paymentMethod, PaymentSource paymentSource);
    PaymentResponse createPayment(int bookingId);
    Payment cancelPaymentByBookingId(int bookingId);
    Payment refundPaymentByBookingId(int bookingId);
    List<Payment> findByHotelId(int hotelId);
    PaymentReport getReport(int hotelId, LocalDate from, LocalDate to);
    int backfillHotelId();

}
