package com.example.realestateagency_back.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}