package com.nawasenahost.availabilityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleHotelNotFound(RoomNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public AvailabilityErrorResponse handleRoomNotAvailable(RoomNotAvailableException e) {

        AvailabilityErrorResponse error = new AvailabilityErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AvailabilityErrorResponse handleIllegalArgument(IllegalArgumentException e) {

        AvailabilityErrorResponse error = new AvailabilityErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AvailabilityErrorResponse handleAccessDenied(AccessDeniedException e) {

        AvailabilityErrorResponse error = new AvailabilityErrorResponse();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

}
