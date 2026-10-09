package com.nawasenahost.paymentservice.service;

import com.nawasenahost.paymentservice.dto.PaymentRequest;
import com.nawasenahost.paymentservice.entity.Payment;
import com.nawasenahost.paymentservice.entity.PaymentStatus;

import java.util.List;

public interface PaymentService {

    List<Payment> findAll();
    Payment findById(int id);
    void deleteById(int id);
    Payment update(int paymentId, PaymentRequest paymentRequest);
    Payment save(PaymentRequest paymentRequest);
    Payment confirmPaymentCash(int paymentId);
    PaymentRequest createPayment(int bookingId);

}
