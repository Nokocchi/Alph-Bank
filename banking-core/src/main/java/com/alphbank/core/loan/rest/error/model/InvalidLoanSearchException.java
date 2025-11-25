package com.alphbank.core.loan.rest.error.model;

public class InvalidLoanSearchException extends RuntimeException {

    public InvalidLoanSearchException() {
        super();
    }

    public InvalidLoanSearchException(String message) {
        super(message);
    }

    public InvalidLoanSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
