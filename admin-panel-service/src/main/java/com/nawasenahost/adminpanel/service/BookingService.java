package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.BookingRequest;
import com.nawasenahost.adminpanel.dto.BookingResponse;
import com.nawasenahost.adminpanel.dto.BookingSummary;
import com.nawasenahost.adminpanel.dto.CustomerResponse;
import com.nawasenahost.adminpanel.dto.SessionData;

import java.util.List;

public interface BookingService {

    List<BookingSummary> findAll(SessionData sessionData);
    BookingSummary findById(SessionData sessionData, int bookingId);
    CustomerResponse findCustomer(SessionData sessionData, int customerId);
    BookingResponse create(SessionData sessionData, BookingRequest bookingRequest);
    BookingSummary cancelPending(SessionData sessionData, int bookingId);

}
