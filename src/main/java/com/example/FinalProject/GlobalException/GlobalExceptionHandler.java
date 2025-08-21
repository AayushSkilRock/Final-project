package com.example.FinalProject.GlobalException;


import com.example.FinalProject.Exception.AccountNotFoundException;
import com.example.FinalProject.Exception.InsufficientBalanceException;
import com.example.FinalProject.Exception.TransactionNotFoundException;
import com.example.FinalProject.Exception.UserNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> TransactionNotFoundException(TransactionNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
