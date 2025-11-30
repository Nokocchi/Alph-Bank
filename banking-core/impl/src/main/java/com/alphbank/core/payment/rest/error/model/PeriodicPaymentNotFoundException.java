package com.alphbank.core.payment.rest.error.model;

import java.util.UUID;

public class PeriodicPaymentNotFoundException extends RuntimeException {

    public PeriodicPaymentNotFoundException(UUID periodicPaymentId) {
        super(String.format("Periodic payment with id %s not found", periodicPaymentId));
    }


}
