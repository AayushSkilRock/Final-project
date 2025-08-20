package com.example.FinalProject.Exception;

public class InvalidInputException extends RuntimeException {
        private String message;
        public InvalidInputException(String msg) {
            super(msg);
            this.message = msg;
        }
}
