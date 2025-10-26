package com.alphbank.core.account.service.model;

import javax.money.MonetaryAmount;
import java.util.UUID;

public record LoanPayoutRequest(MonetaryAmount principal, UUID debtorAccountId, UUID loanReference) {

}