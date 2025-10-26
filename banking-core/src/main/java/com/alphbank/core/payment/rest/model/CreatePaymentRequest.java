package com.alphbank.core.payment.rest.model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(UUID fromCustomerId, UUID fromAccountId, String recipientIban,
                                   MonetaryAmount amount, String messageToSelf,
                                   String messageToRecipient, LocalDateTime scheduledDateTime) {
}
