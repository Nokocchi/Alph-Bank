    package com.alphbank.coreloanservice.service.client.coreaccountservice.model;

import javax.money.MonetaryAmount;
import java.util.UUID;

    public record PayoutLoanRequest(MonetaryAmount principal, UUID debtorAccountId, UUID loanReference) {

    }
