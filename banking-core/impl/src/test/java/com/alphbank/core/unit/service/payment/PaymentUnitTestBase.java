package com.alphbank.core.unit.service.payment;

import com.alphbank.core.payment.service.model.Payment;

public class PaymentUnitTestBase {

    protected Payment createPayment() {
        return Payment.builder()
                .build();
    }
}
