package com.alphbank.coreloanservice.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Payment>
public record SearchLoansResponse(List<Loan> loans) {
}
