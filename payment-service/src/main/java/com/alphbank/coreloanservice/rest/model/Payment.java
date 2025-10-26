package com.alphbank.coreloanservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Payment {

    private UUID paymentId;
    private UUID fromAccountId;
    private UUID basketId;
    private String recipientIban;
    private String messageToSelf;
    private String messageToRecipient;
    private MonetaryAmount paymentAmount;
    private LocalDateTime scheduledDateTime;

}
