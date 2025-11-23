package com.alphbank.commons.impl;

import lombok.Getter;

public class FieldValidationException extends RuntimeException {

    @Getter
    private final FieldValidationResult result;

    public FieldValidationException(FieldValidationResult result) {
        super("Custom validation failed for request body");
        this.result = result;
    }
}
