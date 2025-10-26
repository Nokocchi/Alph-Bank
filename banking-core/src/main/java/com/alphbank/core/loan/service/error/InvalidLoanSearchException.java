package com.alphbank.core.loan.service.error;

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
