package com.nawasenahost.bookingservice.exception;

public class BookingCannotBeConfirmedException extends RuntimeException {
    public BookingCannotBeConfirmedException(String message) {
        super(message);
    }
}
