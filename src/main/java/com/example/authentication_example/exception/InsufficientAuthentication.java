package com.example.authentication_example.exception;

public class InsufficientAuthentication extends RuntimeException {
    public InsufficientAuthentication(String message) {
        super(message);
    }

    public InsufficientAuthentication(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientAuthentication(Throwable cause) {
        super(cause);
    }
}
