package com.nawasenahost.bookingservice.exception;

public class BookingCannotBeUpdatedException extends RuntimeException {
    public BookingCannotBeUpdatedException(String message) {
        super(message);
    }
}
