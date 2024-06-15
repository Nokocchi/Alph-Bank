    package com.alphbank.coreaccountservice.rest.internal.model;

import jakarta.validation.Valid;

import javax.money.MonetaryAmount;
import java.util.UUID;

@Valid
public record AccountTransferRequest(MonetaryAmount remittanceAmount, UUID debtorAccountId, String recipientIban, UUID paymentReference) {

}
