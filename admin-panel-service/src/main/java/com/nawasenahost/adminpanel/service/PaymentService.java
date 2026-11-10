package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.PaymentResponse;
import com.nawasenahost.adminpanel.dto.SessionData;

public interface PaymentService {

    PaymentResponse findByBookingId(SessionData sessionData, int bookingId);
    PaymentResponse confirm(SessionData sessionData, int paymentId, String paymentMethod, String paymentSource);

}
