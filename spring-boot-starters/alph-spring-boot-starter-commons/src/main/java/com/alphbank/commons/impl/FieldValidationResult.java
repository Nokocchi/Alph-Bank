package com.alphbank.commons.impl;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FieldValidationResult {

    @Getter
    private final List<FieldValidationError> errors = new ArrayList<>();

    public void addError(String field, String error) {
        errors.add(new FieldValidationError(field, error));
    }

    public boolean isError() {
        return !errors.isEmpty();
    }
}
