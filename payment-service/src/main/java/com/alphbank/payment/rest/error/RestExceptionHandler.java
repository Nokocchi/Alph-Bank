package com.alphbank.payment.rest.error;

import com.alphbank.commons.impl.AlphExceptionHandler;
import com.alphbank.payment.rest.error.model.BasketCreationException;
import com.alphbank.payment.rest.error.model.BasketNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends AlphExceptionHandler {

    @ExceptionHandler(BasketCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBasketCreationException(BasketCreationException ex) {
        return createError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BasketNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBasketCreationException(BasketNotFoundException ex) {
        return createError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

}
