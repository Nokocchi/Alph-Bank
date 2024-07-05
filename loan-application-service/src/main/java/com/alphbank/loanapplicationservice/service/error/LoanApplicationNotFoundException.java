package com.alphbank.loanapplicationservice.service.error;

public class LoanApplicationNotFoundException extends RuntimeException {

    public LoanApplicationNotFoundException() {
        super();
    }

    public LoanApplicationNotFoundException(String message) {
        super(message);
    }

    public LoanApplicationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
