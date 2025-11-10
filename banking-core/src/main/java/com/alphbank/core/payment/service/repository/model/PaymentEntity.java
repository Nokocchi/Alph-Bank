package com.alphbank.core.payment.service.repository.model;

import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
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

    @Column("monetary_value")
    private BigDecimal monetaryValue;

    @Column("currency")
    private String currency;

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
    @Column("execution_date_time")
    private LocalDateTime executionDateTime;


    public static PaymentEntity from(CreatePaymentRequestDTO createPaymentRequest) {
        LocalDateTime scheduledDateTimeNullable = createPaymentRequest.getScheduledDateTime();
        return PaymentEntity.builder()
                .fromAccountId(createPaymentRequest.getFromAccountId())
                .fromCustomerId(createPaymentRequest.getFromCustomerId())
                .messageToSelf(createPaymentRequest.getMessageToSelf())
                .messageToRecipient(createPaymentRequest.getMessageToRecipient())
                .recipientIban(createPaymentRequest.getRecipientIban())
                .monetaryValue(createPaymentRequest.getAmount().getNumber().numberValue(BigDecimal.class))
                .currency(createPaymentRequest.getAmount().getCurrency().getCurrencyCode())
                .scheduledDateTime(scheduledDateTimeNullable != null ? scheduledDateTimeNullable : LocalDateTime.now())
                .build();
    }
}
