package com.alphbank.coreloanservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Loan {

    private UUID loanId;
    private UUID customerId;
    private UUID accountId;
    private boolean paidOut;
    private MonetaryAmount loanAmount;
    private BigDecimal fixedRateInterest;
    private int loanPeriodMonths;

}
