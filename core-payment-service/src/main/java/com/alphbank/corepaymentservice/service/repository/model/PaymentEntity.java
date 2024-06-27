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

    @Column("from_customer_id")
    private UUID fromCustomerId;

    @Column("from_account_id")
    private UUID fromAccountId;

    //CreatedTimestamp
    //private Timestamp? executeTime

    @Column("remittance_amount")
    private BigDecimal remittanceAmount;

    @Column("remittance_currency")
    private String remittanceCurrency;

    @Column("recipient_iban")
    private String recipientIban;

    @Column("recipient_account_id")
    private UUID recipientAccountId;

    @Column("message_to_self")
    private String messageToSelf;

    @Column("message_to_recipient")
    private String messageToRecipient;

    @Column("scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    @With
    @Column("executed")
    private boolean executed;

    @With
    @Column("execution_date_time")
    private LocalDateTime executionDateTime;


    public static PaymentEntity from(CreatePaymentRequest createPaymentRequest) {
        LocalDateTime scheduledDateTimeNullable = createPaymentRequest.scheduledDateTime();
        return PaymentEntity.builder()
                .fromAccountId(createPaymentRequest.fromAccountId())
                .fromCustomerId(createPaymentRequest.fromCustomerId())
                .messageToSelf(createPaymentRequest.messageToSelf())
                .messageToRecipient(createPaymentRequest.messageToRecipient())
                .recipientIban(createPaymentRequest.recipientIban())
                .remittanceAmount(createPaymentRequest.remittanceAmount().getNumber().numberValueExact(BigDecimal.class))
                .remittanceCurrency(createPaymentRequest.remittanceAmount().getCurrency().getCurrencyCode())
                .scheduledDateTime(scheduledDateTimeNullable != null ? scheduledDateTimeNullable : LocalDateTime.now())
                .build();
    }
}
