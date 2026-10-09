package com.nawasenahost.bookingservice.exception;

public class BookingCannotBeCancelledException extends RuntimeException {
    public BookingCannotBeCancelledException(String message) {
        super(message);
    }
}
