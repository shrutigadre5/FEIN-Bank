package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)  // Tells Spring to return 400
public class TransactionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransactionException(String message) {
        super(message);
    }
}
