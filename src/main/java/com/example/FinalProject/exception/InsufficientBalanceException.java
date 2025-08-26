package com.example.FinalProject.exception;

public class InsufficientBalanceException extends RuntimeException{
    private String message;
    public InsufficientBalanceException(String msg) {
        super(msg);
        this.message = msg;
    }
}