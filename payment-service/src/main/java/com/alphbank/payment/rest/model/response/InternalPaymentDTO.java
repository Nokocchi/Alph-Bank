package com.alphbank.payment.rest.model.response;

import lombok.Builder;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record InternalPaymentDTO(UUID paymentId,
                                 UUID basketId,
                                 UUID fromAccountIban,
                                 String recipientIban,
                                 String messageToSelf,
                                 String messageToRecipient,
                                 MonetaryAmount paymentAmount,
                                 LocalDateTime scheduledDateTime) {
}
