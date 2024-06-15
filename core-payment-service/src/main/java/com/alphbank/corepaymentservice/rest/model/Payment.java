package com.alphbank.corepaymentservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Payment {

    private UUID paymentId;
    private UUID customerId;
    private UUID accountId;
    private boolean executed;
    private MonetaryAmount remittanceAmount;
    private String recipientIban;
    private String messageToSelf;

}
