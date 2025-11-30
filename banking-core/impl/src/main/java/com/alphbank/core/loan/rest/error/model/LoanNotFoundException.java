package com.alphbank.core.loan.rest.error.model;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException() {
        super();
    }

    public LoanNotFoundException(String message) {
        super(message);
    }

    public LoanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
