package com.example.FinalProject.Exception;

public class InsufficientBalanceException extends RuntimeException{
    private String message;
    public InsufficientBalanceException(String msg) {
        super(msg);
        this.message = msg;
    }
}
