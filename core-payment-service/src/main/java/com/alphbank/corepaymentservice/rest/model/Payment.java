package com.alphbank.corepaymentservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Payment {

    private UUID paymentId;
    private UUID fromCustomerId;
    private UUID fromAccountId;
    private boolean executed;
    private MonetaryAmount remittanceAmount;
    private String recipientIban;
    private UUID recipientAccountId;
    private String messageToSelf;
    private String messageToRecipient;
    private LocalDateTime executedDateTime;
    private LocalDateTime scheduledDateTime;

}
