    package com.alphbank.loanapplicationservice.service.client.coreloanservice.model;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.UUID;

    public record CreateLoanRequest(UUID customerId,
                                    UUID accountId,
                                    MonetaryAmount principal,
                                    BigDecimal fixedRateInterestAPR,
                                    int loanTermMonths) {
    }
