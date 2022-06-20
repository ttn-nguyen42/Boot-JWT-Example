package com.example.authenticationexample.exception;

public class InvalidRefreshToken extends RuntimeException {
    public InvalidRefreshToken(String message) {
        super(message);
    }

    public InvalidRefreshToken(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRefreshToken(Throwable cause) {
        super(cause);
    }
}
