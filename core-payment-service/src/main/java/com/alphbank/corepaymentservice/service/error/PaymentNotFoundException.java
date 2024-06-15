package com.alphbank.corepaymentservice.service.error;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID paymentId) {
        super(String.format("Payment with id %s not found", paymentId));
    }


}
