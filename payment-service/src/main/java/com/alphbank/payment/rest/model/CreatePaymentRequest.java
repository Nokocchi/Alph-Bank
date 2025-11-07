package com.alphbank.payment.rest.model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(UUID fromCustomerId,
                                   UUID fromAccountId,
                                   String recipientIban,
                                   String messageToSelf,
                                   String messageToRecipient,
                                   MonetaryAmount amount,
                                   LocalDateTime scheduledDateTime) {
}
