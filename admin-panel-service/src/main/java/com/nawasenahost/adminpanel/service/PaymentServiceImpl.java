package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.PaymentResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final WebClient webClient;

    public PaymentServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public PaymentResponse findByBookingId(SessionData sessionData, int bookingId) {

        try {
            return webClient.get()
                    .uri("/api/payments/booking/{bookingId}", bookingId)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    @Override
    public PaymentResponse confirm(SessionData sessionData,
                                   int paymentId,
                                   String paymentMethod,
                                   String paymentSource) {

        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/payments/confirm-payment")
                            .queryParam("paymentId", paymentId)
                            .queryParam("paymentMethod", paymentMethod)
                            .queryParam("paymentSource", paymentSource)
                            .build())
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
