package com.alphbank.coreloanservice.rest;

import com.alphbank.coreloanservice.service.error.LoanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LoanNotFoundException.class)
    public void handleLoanNotFoundException(LoanNotFoundException exception){
        System.out.print("not found..");
    }
}
