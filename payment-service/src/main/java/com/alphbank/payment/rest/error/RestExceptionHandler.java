package com.alphbank.payment.rest.error;

import com.alphbank.commons.impl.AlphExceptionHandler;
import com.alphbank.payment.rest.error.model.BasketCreationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends AlphExceptionHandler {

    @ExceptionHandler(BasketCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBasketCreationException(BasketCreationException ex) {
        return createError(HttpStatus.BAD_REQUEST, "Basket issues");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleWebExchangeBindException(WebExchangeBindException ex) {
        return Map.of(
                "WebExchangeBindException", "Unknown error"
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
        return Map.of(
                "ConstraintViolationException", "Unknown error"
        );
    }


}
