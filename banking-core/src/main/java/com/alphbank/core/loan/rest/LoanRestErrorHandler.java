package com.alphbank.core.loan.rest;

import com.alphbank.core.loan.service.error.InvalidLoanSearchException;
import com.alphbank.core.loan.service.error.LoanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class LoanRestErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LoanNotFoundException.class)
    public void handleLoanNotFoundException(LoanNotFoundException exception){
        System.out.print("not found..");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidLoanSearchException.class)
    public void handleInvalidLoanSearchException(InvalidLoanSearchException exception){
        System.out.print(exception.getMessage());
    }
}
