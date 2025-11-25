package com.alphbank.core.account.rest.error;

import com.alphbank.commons.impl.AlphExceptionHandler;
import com.alphbank.core.account.rest.error.model.AccountNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class AccountRestErrorHandler extends AlphExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ErrorResponse handleAccountNotFound(AccountNotFoundException exception) {
        return createError(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
