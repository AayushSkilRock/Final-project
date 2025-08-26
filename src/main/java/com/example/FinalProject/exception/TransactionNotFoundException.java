package com.example.FinalProject.exception;

public class TransactionNotFoundException extends RuntimeException{
    private String message;
    public TransactionNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
