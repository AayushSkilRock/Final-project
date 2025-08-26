package com.example.FinalProject.exception;

public class AccessDeniedException extends RuntimeException {
    private String message;
    public AccessDeniedException(String msg) {
        super(msg);
        this.message = msg;
    }
}
