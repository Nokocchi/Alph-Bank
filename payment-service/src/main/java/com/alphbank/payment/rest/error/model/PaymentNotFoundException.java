package com.alphbank.payment.rest.error.model;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(UUID paymentId) {
        super("Payment with id %s not found".formatted(paymentId));
    }
}
