package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.PaymentReport;
import com.nawasenahost.adminpanel.dto.PaymentResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final GatewayClient gatewayClient;

    public PaymentServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<PaymentResponse> findAll(SessionData sessionData) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<PaymentResponse>>() {},
                "/api/payments/hotel/{hotelId}", sessionData.hotelId());
    }

    @Override
    public PaymentResponse findByBookingId(SessionData sessionData, int bookingId) {
        return gatewayClient.get(sessionData, PaymentResponse.class, "/api/payments/booking/{bookingId}", bookingId);
    }

    @Override
    public PaymentResponse confirm(SessionData sessionData,
                                   int paymentId,
                                   String paymentMethod,
                                   String paymentSource) {
        return gatewayClient.post(sessionData, null, PaymentResponse.class,
                "/api/payments/confirm-payment?paymentId={paymentId}&paymentMethod={paymentMethod}&paymentSource={paymentSource}",
                paymentId, paymentMethod, paymentSource);
    }

    @Override
    public PaymentReport getReport(SessionData sessionData, LocalDate from, LocalDate to) {
        return gatewayClient.get(sessionData, PaymentReport.class,
                "/api/payments/hotel/{hotelId}/report?from={from}&to={to}", sessionData.hotelId(), from, to);
    }
}
