package com.example.authentication_example.exception;

public class UserAlreadyExist extends RuntimeException{
    public UserAlreadyExist() {
    }

    public UserAlreadyExist(String message) {
        super(message);
    }

    public UserAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExist(Throwable cause) {
        super(cause);
    }
}
