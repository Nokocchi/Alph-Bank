package com.alphbank.loanapplicationservice.service.repository.model;

import com.alphbank.loanapplicationservice.rest.model.ApplicationStatus;
import com.alphbank.loanapplicationservice.rest.model.CreateLoanApplicationRequest;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Table("loanapplication")
public class LoanApplicationEntity {

    @Column("id")
    @Id
    private UUID loanApplicationId;

    @Column("customer_id")
    private UUID customerId;

    @Column("account_id")
    private UUID accountId;

    @With
    @Column("signing_session_id")
    private UUID signingSessionId;

    @Column("national_id")
    private String nationalId;

    @Column("country_code")
    private String countryCode;

    @Column("locale")
    private String locale;

    @Column("principal")
    private BigDecimal principal;

    @Column("currency")
    private String currency;

    @Column("fixed_rate_interest_apr")
    private BigDecimal fixedRateInterestAPR;

    @Column("loan_term_months")
    private int loanPeriodMonths;

    @With
    @Builder.Default
    @Column("application_status")
    private String applicationStatus = ApplicationStatus.CREATED.toString();


    public static LoanApplicationEntity from(CreateLoanApplicationRequest createLoanApplicationRequest) {
        return LoanApplicationEntity.builder()
                .customerId(createLoanApplicationRequest.customerId())
                .accountId(createLoanApplicationRequest.accountId())
                .nationalId(createLoanApplicationRequest.nationalId())
                .countryCode(createLoanApplicationRequest.locale().getCountry())
                .locale(createLoanApplicationRequest.locale().getLanguage())
                .principal(createLoanApplicationRequest.principal().getNumber().numberValue(BigDecimal.class))
                .currency(createLoanApplicationRequest.principal().getCurrency().getCurrencyCode())
                .fixedRateInterestAPR(createLoanApplicationRequest.fixedRateInterestAPR())
                .loanPeriodMonths(createLoanApplicationRequest.loanTermMonths())
                .build();
    }
}
