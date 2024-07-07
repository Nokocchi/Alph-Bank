    package com.alphbank.coreloanservice.service.client.corepaymentservice.model;

    import javax.money.MonetaryAmount;
    import java.time.LocalDateTime;
    import java.util.UUID;

    public record CreateCorePaymentRequest(UUID fromCustomerId, UUID fromAccountId, String recipientIban,
                                           MonetaryAmount remittanceAmount, String messageToSelf,
                                           String messageToRecipient, LocalDateTime scheduledDateTime) {
    }
