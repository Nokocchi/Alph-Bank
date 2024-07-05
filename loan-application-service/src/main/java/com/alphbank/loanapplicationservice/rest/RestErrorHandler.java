package com.alphbank.loanapplicationservice.rest;

import com.alphbank.loanapplicationservice.service.error.LoanApplicationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LoanApplicationNotFoundException.class)
    public void handleLoanApplicationNotFoundException(LoanApplicationNotFoundException exception){
        System.out.print("not found..");
    }

}
