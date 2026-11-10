package com.nawasenahost.adminpanel.exception;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
