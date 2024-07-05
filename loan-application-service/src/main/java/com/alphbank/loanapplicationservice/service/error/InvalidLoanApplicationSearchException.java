package com.alphbank.loanapplicationservice.service.error;

public class InvalidLoanApplicationSearchException extends RuntimeException {

    public InvalidLoanApplicationSearchException() {
        super();
    }

    public InvalidLoanApplicationSearchException(String message) {
        super(message);
    }

    public InvalidLoanApplicationSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
