package com.alphbank.core.customer.rest;

import com.alphbank.core.customer.service.error.CustomerNotFoundException;
import com.alphbank.core.customer.service.error.DuplicateCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomerRestErrorHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public String handleCustomerNotFoundException(CustomerNotFoundException exception) {
        return "NOT FOUND!!";
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateCustomerException.class)
    public String handleDuplicateCustomerException(DuplicateCustomerException exception) {
        return exception.getMessage();
    }
}
