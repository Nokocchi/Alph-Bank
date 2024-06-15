package com.alphbank.corecustomerservice.rest;

import com.alphbank.corecustomerservice.service.error.CustomerNotFoundException;
import com.alphbank.corecustomerservice.service.error.CustomerWithGovIdAndCountryCodeAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public String handleCustomerNotFoundException(CustomerNotFoundException exception){
        return "NOT FOUND!!";
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomerWithGovIdAndCountryCodeAlreadyExistsException.class)
    public String handleCustomerWithGovIdAndCountryCodeAlreadyExistsException(CustomerWithGovIdAndCountryCodeAlreadyExistsException exception){
        return exception.getMessage();
    }
}
