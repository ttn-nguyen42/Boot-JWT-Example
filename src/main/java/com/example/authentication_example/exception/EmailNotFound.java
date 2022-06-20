package com.example.authentication_example.exception;

public class EmailNotFound extends RuntimeException {
    public EmailNotFound() {
        super();
    }

    public EmailNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFound(String message) {
        super(message);
    }

    public EmailNotFound(Throwable cause) {
        super(cause);
    }
}
