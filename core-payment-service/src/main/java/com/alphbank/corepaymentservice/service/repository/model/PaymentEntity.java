package com.alphbank.corepaymentservice.service.repository.model;

import com.alphbank.corepaymentservice.rest.model.CreatePaymentRequest;
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
@Table("payment")
public class PaymentEntity {

    @Column("id")
    @Id
    private UUID paymentId;

    @Column("customer_id")
    private UUID customerId;

    @Column("account_id")
    private UUID accountId;

    //CreatedTimestamp
    //private Timestamp? executeTime

    @With
    @Column("executed")
    private boolean executed;

    @Column("remittance_amount")
    private BigDecimal remittanceAmount;

    @Column("remittance_currency")
    private String remittanceCurrency;

    @Column("recipient_iban")
    private String recipientIban;

    @Column("message_to_self")
    private String messageToSelf;

    @Column("execution_date_time")
    private LocalDateTime executionDateTime;


    public static PaymentEntity from(CreatePaymentRequest createPaymentRequest) {
        return PaymentEntity.builder()
                .accountId(createPaymentRequest.accountId())
                .customerId(createPaymentRequest.customerId())
                .messageToSelf(createPaymentRequest.messageToSelf())
                .recipientIban(createPaymentRequest.recipientIban())
                .remittanceAmount(createPaymentRequest.remittanceAmount().getNumber().numberValueExact(BigDecimal.class))
                .remittanceCurrency(createPaymentRequest.remittanceAmount().getCurrency().getCurrencyCode())
                .executionDateTime(createPaymentRequest.getExecutionDateTime())
                .build();
    }
}
