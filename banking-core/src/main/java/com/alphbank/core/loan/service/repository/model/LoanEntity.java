package com.alphbank.core.loan.service.repository.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table("loan")
public class LoanEntity {

    @Column("id")
    @Id
    private UUID loanId;

    @Column("customer_id")
    private UUID customerId;

    @Column("account_id")
    private UUID accountId;

    @Column("principal")
    private BigDecimal principal;

    @Column("currency")
    private String currency;

    @Column("fixed_rate_interest_apr")
    private BigDecimal fixedRateInterestAPR;

    @Column("loan_term_months")
    private int loanPeriodMonths;

    @With
    @Column("payout_date_time")
    private LocalDateTime payoutDateTime;

    /*
    public static LoanEntity from(CreateLoanRequest createLoanRequest) {
        return LoanEntity.builder()
                .accountId(createLoanRequest.accountId())
                .customerId(createLoanRequest.customerId())
                .principal(createLoanRequest.principal().getNumber().numberValue(BigDecimal.class))
                .currency(createLoanRequest.principal().getCurrency().getCurrencyCode())
                .fixedRateInterestAPR(createLoanRequest.fixedRateInterestAPR())
                .loanPeriodMonths(createLoanRequest.loanTermMonths())
                .build();
    }*/
}
