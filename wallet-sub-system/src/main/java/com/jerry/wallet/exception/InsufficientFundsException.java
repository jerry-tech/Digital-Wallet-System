package com.jerry.wallet.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ErrorResponse {
    public InsufficientFundsException() {
        super(HttpStatus.BAD_REQUEST, "Insufficient funds in source wallet");
    }
}