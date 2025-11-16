package com.alphbank.payment.service.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Setter
@Getter
@Accessors(fluent = true)
public class PeriodicPayment {

    private UUID id;
    private UUID basketId;
    private UUID coreReference;

    @NotNull
    private final String fromAccountIban;

    @NotNull
    private final String recipientIban;

    @NotNull
    private final String messageToSelf;

    @NotNull
    private final String messageToRecipient;

    @NotNull
    private final MonetaryAmount monetaryAmount;

    private String recipientName;

    private LocalDate startDate;

    private LocalDate endDate;

    private PeriodicPaymentFrequency frequency;

    private UUID requestId;

    private String psuIPAddress;

}
