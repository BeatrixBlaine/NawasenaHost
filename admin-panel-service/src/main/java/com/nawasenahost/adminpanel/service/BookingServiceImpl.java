package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.CustomerResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final WebClient webClient;

    public BookingServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BookingSummary> findAll(SessionData sessionData) {

        try {
            return webClient.get()
                    .uri("/api/bookings/hotel/{hotelId}", sessionData.hotelId())
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<BookingSummary>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    @Override
    public BookingSummary findById(SessionData sessionData, int bookingId) {

        try {
            return webClient.get()
                    .uri("/api/bookings/{bookingId}", bookingId)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(BookingSummary.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    // customer name is only a nice to have, the page still works without it
    @Override
    public CustomerResponse findCustomer(SessionData sessionData, int customerId) {

        try {
            return webClient.get()
                    .uri("/api/customers/{customerId}", customerId)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(CustomerResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            return null;
        }
    }

    @Override
    public BookingResponse create(SessionData sessionData, BookingRequest bookingRequest) {

        // the hotel always comes from the token of the user, never from the form
        bookingRequest.getBookingData().setHotelId(sessionData.hotelId());

        try {
            return webClient.post()
                    .uri("/api/customers/create-new-booking")
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .bodyValue(bookingRequest)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    @Override
    public BookingSummary cancelPending(SessionData sessionData, int bookingId) {

        try {
            return webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/bookings/cancel-pending-booking")
                            .queryParam("bookingId", bookingId)
                            .build())
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(BookingSummary.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
