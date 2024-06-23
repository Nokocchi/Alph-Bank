package com.alphbank.corepaymentservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentSearchResult {

    private UUID paymentId;
    private UUID fromCustomerId;
    private UUID fromAccountId;
    private boolean executed;
    private MonetaryAmount remittanceAmount;
    private String recipientIban;
    private UUID recipientAccountId;
    private String message;
    private LocalDateTime visibleOnAccountDateTime;

}
