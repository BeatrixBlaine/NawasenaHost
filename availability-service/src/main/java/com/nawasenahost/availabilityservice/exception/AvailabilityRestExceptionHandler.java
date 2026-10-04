package com.nawasenahost.availabilityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AvailabilityRestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<AvailabilityErrorResponse> handleException(AvailabilityNotFoundException exc) {

        // create a RoomErrorResponse
        AvailabilityErrorResponse error = new AvailabilityErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        // return ResponseEntity
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AvailabilityErrorResponse> handleException(
            MethodArgumentNotValidException exc) {

        AvailabilityErrorResponse error = new AvailabilityErrorResponse();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getBindingResult().getFieldError().getDefaultMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
