package com.alphbank.corepaymentservice.rest;

import com.alphbank.corepaymentservice.service.error.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PaymentNotFoundException.class)
    public String handlePaymentNotFoundException(PaymentNotFoundException exception){
        return exception.getMessage();
    }
}
