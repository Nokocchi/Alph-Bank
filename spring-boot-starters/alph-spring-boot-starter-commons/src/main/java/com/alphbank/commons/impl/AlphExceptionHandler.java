package com.alphbank.commons.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.MissingRequestValueException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AlphExceptionHandler {

    // If a method argument is annotated with a custom validation annotation, and if that validation fails, it throws a HandlerMethodValidationException
    // If the class is annotated with @Validated, then the thrown exception is instead WebExchangeBindException.
    // Don't know if I should extend from ResponseEntityExceptionHandler and override the few default exception handlers? Or just roll my own? Time will tell.

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private FieldErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, List<String>> errors = ex.getParameterValidationResults()
                .stream()
                .map(ParameterValidationResult::getResolvableErrors)
                .flatMap(List::stream)
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(f -> Optional.ofNullable(f.getDefaultMessage()).orElse(""), Collectors.toList())));

        List<FieldErrorEntry> errorList = errors.entrySet().stream()
                .map(e -> new FieldErrorEntry(e.getKey(), e.getValue()))
                .toList();

        return new FieldErrorResponse(ex.getStatusCode().value(), errorList);
    }

    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestValueException(MissingRequestValueException ex){
        return createError(HttpStatus.BAD_REQUEST, ex.getReason());
    }

    private record FieldErrorResponse(int errorCode, List<FieldErrorEntry> errors) {
    }

    private record FieldErrorEntry(String fieldName, List<String> errors) {
    }

    public record ErrorResponse(int errorCode, String errorMessage) {
    }

    protected ErrorResponse createError(HttpStatusCode status, String errorMessage){
        return new ErrorResponse(status.value(), errorMessage);
    }
}
