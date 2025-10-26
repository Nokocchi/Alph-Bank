package com.alphbank.core.account.service.model;

import javax.money.MonetaryAmount;
import java.util.UUID;

public record AccountTransferRequest(MonetaryAmount amount, UUID debtorAccountId, String recipientIban, UUID paymentReference) {

}