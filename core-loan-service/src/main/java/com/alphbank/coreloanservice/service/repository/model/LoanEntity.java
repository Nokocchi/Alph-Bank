package com.alphbank.coreloanservice.service.repository.model;

import com.alphbank.coreloanservice.rest.model.CreateLoanRequest;
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

    //CreatedTimestamp
    //private Timestamp? executeTime

    @With
    @Column("paid_out")
    private boolean paidOut;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private String currency;

    @Column("fixed_rate_interest")
    private BigDecimal fixedRateInterest;

    @Column("loan_period_months")
    private int loanPeriodMonths;

    @Column("payout_date_time")
    private LocalDateTime payoutDateTime;

    public static LoanEntity from(CreateLoanRequest createLoanRequest) {
        return LoanEntity.builder()
                .accountId(createLoanRequest.accountId())
                .customerId(createLoanRequest.customerId())
                .amount(createLoanRequest.loanAmount().getNumber().numberValue(BigDecimal.class))
                .currency(createLoanRequest.loanAmount().getCurrency().getCurrencyCode())
                .fixedRateInterest(createLoanRequest.fixedRateInterest())
                .loanPeriodMonths(createLoanRequest.loanPeriodMonths())
                .payoutDateTime(createLoanRequest.payoutDateTime())
                .build();
    }
}
