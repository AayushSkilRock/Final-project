package com.example.FinalProject.Exception;

public class UserNotFoundException extends RuntimeException {
    private String message;

    public UserNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
