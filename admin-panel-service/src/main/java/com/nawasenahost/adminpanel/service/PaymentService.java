package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.PaymentReport;
import com.nawasenahost.adminpanel.dto.PaymentResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    List<PaymentResponse> findAll(SessionData sessionData);
    PaymentResponse findByBookingId(SessionData sessionData, int bookingId);
    PaymentResponse confirm(SessionData sessionData, int paymentId, String paymentMethod, String paymentSource);
    PaymentReport getReport(SessionData sessionData, LocalDate from, LocalDate to);

}
