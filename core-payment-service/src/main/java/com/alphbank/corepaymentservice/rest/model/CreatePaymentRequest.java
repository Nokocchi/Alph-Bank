package com.alphbank.corepaymentservice.rest.model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(UUID fromCustomerId, UUID fromAccountId, String recipientIban,
                                   MonetaryAmount remittanceAmount, String messageToSelf,
                                   String messageToRecipient, LocalDateTime scheduledDateTime) {
}
