package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.BookingReport;
import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    List<BookingSummary> findAll(SessionData sessionData);
    List<BookingSummary> findByCustomer(SessionData sessionData, int customerId);
    BookingSummary findById(SessionData sessionData, int bookingId);
    BookingReport getReport(SessionData sessionData, LocalDate from, LocalDate to);
    BookingResponse create(SessionData sessionData, BookingRequest bookingRequest);
    BookingSummary cancel(SessionData sessionData, int bookingId, String reason);
    BookingSummary complete(SessionData sessionData, int bookingId);

}
