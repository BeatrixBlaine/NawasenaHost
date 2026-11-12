package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.BookingReport;
import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    private final GatewayClient gatewayClient;

    public BookingServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<BookingSummary> findAll(SessionData sessionData) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<BookingSummary>>() {},
                "/api/bookings/hotel/{hotelId}", sessionData.hotelId());
    }

    @Override
    public List<BookingSummary> findByCustomer(SessionData sessionData, int customerId) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<BookingSummary>>() {},
                "/api/bookings/hotel/{hotelId}/customer/{customerId}", sessionData.hotelId(), customerId);
    }

    @Override
    public BookingSummary findById(SessionData sessionData, int bookingId) {
        return gatewayClient.get(sessionData, BookingSummary.class, "/api/bookings/{bookingId}", bookingId);
    }

    @Override
    public BookingReport getReport(SessionData sessionData, LocalDate from, LocalDate to) {
        return gatewayClient.get(sessionData, BookingReport.class,
                "/api/bookings/hotel/{hotelId}/report?from={from}&to={to}", sessionData.hotelId(), from, to);
    }

    @Override
    public BookingResponse create(SessionData sessionData, BookingRequest bookingRequest) {

        // the hotel always comes from the session of the user, never from the form
        bookingRequest.getBookingData().setHotelId(sessionData.hotelId());

        return gatewayClient.post(sessionData, bookingRequest, BookingResponse.class,
                "/api/customers/create-new-booking");
    }

    @Override
    public BookingSummary cancel(SessionData sessionData, int bookingId, String reason) {
        return gatewayClient.put(sessionData, null, BookingSummary.class,
                "/api/bookings/{bookingId}/cancel?reason={reason}", bookingId, reason == null ? "" : reason);
    }

    @Override
    public BookingSummary complete(SessionData sessionData, int bookingId) {
        return gatewayClient.put(sessionData, null, BookingSummary.class,
                "/api/bookings/{bookingId}/complete", bookingId);
    }
}
