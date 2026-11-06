package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public class BookingRequest {

    private BookingData bookingData;

    private CustomerData customerData;

    public BookingData getBookingData() {
        return bookingData;
    }

    public void setBookingData(BookingData bookingData) {
        this.bookingData = bookingData;
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
    }
}
