package com.alphbank.loanapplicationservice.rest.model;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public record CreateLoanApplicationRequest(UUID customerId,
                                           String nationalId,
                                           UUID accountId,
                                           Locale locale,
                                           MonetaryAmount principal,
                                           BigDecimal fixedRateInterestAPR,
                                           int loanTermMonths,
                                           String onSigningSuccessRedirectUrl,
                                           String onSigningFailedRedirectUrl) {
}
