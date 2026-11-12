package com.nawasenahost.roomservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(HotelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleHotelNotFound(HotelNotFoundException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getBindingResult().getFieldError().getDefaultMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoomNotFoundException(RoomNotFoundException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(HotelMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHotelMismatchException(HotelMismatchException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );
    }

    // thrown when a list of rooms (batch) fails validation
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getConstraintViolations().iterator().next().getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(InventorySyncException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleInventorySyncException(InventorySyncException e) {
        return new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConcurrentChange(Exception e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "The record was changed by someone else or is still in use, please reload and try again",
                System.currentTimeMillis());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                System.currentTimeMillis());
    }

}
