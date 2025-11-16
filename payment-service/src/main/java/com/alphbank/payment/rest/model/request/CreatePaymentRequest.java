package com.alphbank.payment.rest.model.request;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(String fromAccountIban,
                                   String recipientIban,
                                   String messageToSelf,
                                   String messageToRecipient,
                                   MonetaryAmount amount,
                                   LocalDateTime scheduledDateTime) {
}
