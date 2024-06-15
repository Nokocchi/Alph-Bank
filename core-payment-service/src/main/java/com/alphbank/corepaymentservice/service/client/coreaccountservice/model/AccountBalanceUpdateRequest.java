package com.alphbank.corepaymentservice.service.client.coreaccountservice.model;

import javax.money.MonetaryAmount;
import java.util.UUID;

public record AccountBalanceUpdateRequest (MonetaryAmount remittanceAmount, UUID debtorAccountId, String recipientIban, UUID paymentReference) {

}
