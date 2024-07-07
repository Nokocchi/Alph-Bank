package com.alphbank.coreloanservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Payment {

    private UUID paymentId;
    private UUID accountId;
    private UUID basketId;
    private String recipientIBAN;
    private MonetaryAmount paymentAmount;
    private LocalDateTime scheduledDateTime;

}
