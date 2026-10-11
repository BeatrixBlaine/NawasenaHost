package com.nawasenahost.clientservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(ClientNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ClientErrorResponse handlerUserBadRequest(MethodArgumentNotValidException e) {

        ClientErrorResponse error = new ClientErrorResponse();
        error.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimeStamp(System.currentTimeMillis());

        return error;
    }

}
