package com.alphbank.corepaymentservice.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Payment>
public record SearchPaymentResponse(List<Payment> payments) {
}
