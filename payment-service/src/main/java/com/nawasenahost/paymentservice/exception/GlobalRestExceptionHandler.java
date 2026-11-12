package com.nawasenahost.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PaymentErrorResponse handlePaymentNotFound(PaymentNotFoundException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PaymentErrorResponse handleBookingNotFound(BookingNotFoundException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(PaymentConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public PaymentErrorResponse handlePaymentConflict(PaymentConflictException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public PaymentErrorResponse handleOptimisticLock(ObjectOptimisticLockingFailureException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage("The payment was changed by someone else, please reload and try again");
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public PaymentErrorResponse handleIllegalArgument(IllegalArgumentException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public PaymentErrorResponse handleAccessDenied(AccessDeniedException e) {

        PaymentErrorResponse error = new PaymentErrorResponse();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

}
