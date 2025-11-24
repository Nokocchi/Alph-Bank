package com.alphbank.core.customer.rest.error;

import com.alphbank.commons.impl.AlphExceptionHandler;
import com.alphbank.core.customer.rest.error.model.AddressNotFoundException;
import com.alphbank.core.customer.rest.error.model.CustomerNotFoundException;
import com.alphbank.core.customer.rest.error.model.DuplicateCustomerException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomerRestErrorHandler extends AlphExceptionHandler {

    @ExceptionHandler({CustomerNotFoundException.class, AddressNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCustomerNotFoundException(CustomerNotFoundException exception) {
        return createError(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateCustomerException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateCustomerException(DuplicateCustomerException exception) {
        return createError(HttpStatus.CONFLICT, exception.getMessage());
    }
}
