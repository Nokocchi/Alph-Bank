    package com.alphbank.coreaccountservice.rest.internal.model;

    import javax.money.MonetaryAmount;
    import java.util.UUID;

public record AccountTransferRequest(MonetaryAmount remittanceAmount, UUID debtorAccountId, String recipientIban, UUID paymentReference) {

}
