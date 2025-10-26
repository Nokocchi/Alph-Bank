package com.alphbank.core.loan.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Loan {

    private UUID loanId;
    private UUID customerId;
    private UUID accountId;
    private MonetaryAmount principal;
    private BigDecimal fixedRateInterestAPR;
    private int loanTermMonths;
    private LocalDateTime payoutDateTime;

}
