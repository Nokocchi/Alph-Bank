package com.alphbank.payment.rest.error.model;

public class BasketCreationException extends RuntimeException {
    public BasketCreationException(long expectedPaymentsCount, long actualPaymentsCount) {
        super("Tried to create basket with %d payments, but found %d in the database.".formatted(expectedPaymentsCount, actualPaymentsCount));
    }
}
