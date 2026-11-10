package com.nawasenahost.paymentservice.exception;

public class PaymentConflictException extends RuntimeException {
    public PaymentConflictException(String message) {
        super(message);
    }
}
