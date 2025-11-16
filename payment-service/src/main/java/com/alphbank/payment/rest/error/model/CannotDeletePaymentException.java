package com.alphbank.payment.rest.error.model;

import java.util.UUID;

public class CannotDeletePaymentException extends RuntimeException {
    public CannotDeletePaymentException(UUID paymentId) {
        super("Could not delete payment with id %s, as it has an ongoing authorization.".formatted(paymentId));
    }
}
