package com.alphbank.commons.impl;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Don't know if I should extend from ResponseEntityExceptionHandler and override the few default exception handlers? Or just roll my own? Time will tell.
public abstract class AlphExceptionHandler {

    // If a method argument is annotated with a custom ConstraintValidator validation annotation, and if that validation fails, it throws a HandlerMethodValidationException
    // If the class is annotated with @Validated, then the thrown exception is instead WebExchangeBindException.
    // I generate my Controller API interfaces and models, and the generated interface defines the request body as Mono<>.
    // It is not possible to annotate a Mono<> request body with a ConstraintValidator, so this exception handler will never be used.
    // The only exception is the PSD2 API where I could not use the generated API interface, and I have chosen to not wrap the request body in Mono<>.
    // The request body is still generated, so I cannot annotate the model directly, so I annotate the request body parameter in the controller method.
    // In short: This exception handler is used in that specific scenario.
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

    // Called when @Valid on a Mono<> request body fails validation
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FieldErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        Map<String, List<String>> fieldErrorsByField = ex.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(f -> Optional.ofNullable(f.getDefaultMessage()).orElse(""), Collectors.toList())));

        List<FieldErrorEntry> errorList = fieldErrorsByField.entrySet().stream()
                .map(e -> new FieldErrorEntry(e.getKey(), e.getValue()))
                .toList();

        return new FieldErrorResponse(ex.getStatusCode().value(), errorList);
    }

    // This handles missing headers in the request
    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestValueException(MissingRequestValueException ex){
        return createError(HttpStatus.BAD_REQUEST, ex.getReason());
    }

    // This is used when you want to do custom validation on your Mono<> request bodies which cannot be expressed through normal hibernate validation annotations
    // and when you cannot annotate the model/DTO itself, and when you cannot change the method signature to not use Mono<>.
    @ExceptionHandler(FieldValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FieldErrorResponse handleCustomFieldValidationException(FieldValidationException ex) {
        Map<String, List<String>> fieldErrorsByField = ex.getResult()
                .getErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldValidationError::fieldName, Collectors.mapping(FieldValidationError::errorMessage, Collectors.toList())));

        List<FieldErrorEntry> errorList = fieldErrorsByField.entrySet().stream()
                .map(e -> new FieldErrorEntry(e.getKey(), e.getValue()))
                .toList();

        return new FieldErrorResponse(HttpStatus.BAD_REQUEST.value(), errorList);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
        return Map.of(
                "ConstraintViolationException", "Unknown error"
        );
    }

    // If an enum has an invalid value, or in the case of certain validations like @DateTimeFormat, a generic ServerWebInputException is thrown
    // TODO: How do I ensure this only gets run as a backup? WebExchangeBindException and MissingRequestValueException should be tried first
    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(ServerWebInputException ex){
        return createError(HttpStatus.BAD_REQUEST, ex.getCause().getMessage());
    }

    public record FieldErrorResponse(int errorCode, List<FieldErrorEntry> errors) {
    }

    public record FieldErrorEntry(String fieldName, List<String> errors) {
    }

    public record ErrorResponse(int errorCode, String errorMessage) {
    }

    protected ErrorResponse createError(HttpStatusCode status, String errorMessage){
        return new ErrorResponse(status.value(), errorMessage);
    }
}
