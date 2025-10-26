package com.alphbank.loanapplicationservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class LoanApplication {

    private UUID loanApplicationId;
    private UUID customerId;
    private UUID accountId;
    private String nationalId;
    private Locale locale;
    MonetaryAmount principal;
    BigDecimal fixedRateInterestAPR;
    int loanTermMonths;
    ApplicationStatus applicationStatus;
}
