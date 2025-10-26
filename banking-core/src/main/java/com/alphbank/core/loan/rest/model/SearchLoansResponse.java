package com.alphbank.core.loan.rest.model;

import java.util.List;

// Should be deleted and endpoint should return Flux<Payment>
public record SearchLoansResponse(List<Loan> loans) {
}
