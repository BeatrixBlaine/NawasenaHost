package com.nawasenahost.customerservice.dto;

import jakarta.validation.Valid;

public class BookingRequest {

    @Valid
    private CustomerData customerData;

    @Valid
    private BookingData bookingData;

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
    }

    public BookingData getBookingData() {
        return bookingData;
    }

    public void setBookingData(BookingData bookingData) {
        this.bookingData = bookingData;
    }
}
