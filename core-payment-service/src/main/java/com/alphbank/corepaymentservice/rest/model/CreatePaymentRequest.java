package com.alphbank.corepaymentservice.rest.model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(UUID customerId, UUID accountId, MonetaryAmount remittanceAmount, String messageToSelf, String recipientIban, LocalDateTime getExecutionDateTime) {
}
