package com.alphbank.loanapplicationservice.rest.model;

import java.util.List;

public record SearchLoansResponse (List<LoanApplication> loanApplications ) {
}
