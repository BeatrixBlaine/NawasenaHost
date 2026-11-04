package com.nawasenahost.customerservice.dto;

import jakarta.validation.Valid;

public class CreateNewBookingRequest {

    @Valid
    private CustomerRequest customerRequest;

    @Valid
    private BookingRequest bookingRequest;

    public CustomerRequest getCustomerRequest() {
        return customerRequest;
    }

    public void setCustomerRequest(CustomerRequest customerRequest) {
        this.customerRequest = customerRequest;
    }

    public BookingRequest getBookingRequest() {
        return bookingRequest;
    }

    public void setBookingRequest(BookingRequest bookingRequest) {
        this.bookingRequest = bookingRequest;
    }
}
