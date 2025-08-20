package com.example.FinalProject.GlobalException;


import com.example.FinalProject.Exception.AccountNotFoundException;
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
}
