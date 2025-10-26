package com.alphbank.core.payment.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Payment>
public record PaymentSearchRestResponse(List<PaymentSearchResult> payments) {
}
