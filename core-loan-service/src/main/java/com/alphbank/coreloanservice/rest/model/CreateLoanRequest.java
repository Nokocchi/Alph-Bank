package com.alphbank.coreloanservice.rest.model;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateLoanRequest(UUID customerId,
                                UUID accountId,
                                MonetaryAmount loanAmount,
                                BigDecimal fixedRateInterest,
                                int loanPeriodMonths,
                                LocalDateTime payoutDateTime) {
}
