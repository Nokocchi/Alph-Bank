package com.alphbank.coreloanservice.service.repository.model;

import com.alphbank.coreloanservice.rest.model.CreatePaymentRequest;
import lombok.Builder;
import lombok.Data;
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

    @Column("basket_id")
    private UUID basketId;

    @Column("account_id")
    private UUID accountId;

    @Column("recipient_iban")
    private String recipientIBAN;

    @Column("message_to_self")
    private String messageToSelf;

    @Column("message_to_recipient")
    private String messageToRecipient;

    @Column("payment_amount")
    private BigDecimal amount;

    @Column("payment_currency")
    private String currency;

    @Column("scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    public static PaymentEntity from(UUID basketId, CreatePaymentRequest createPaymentRequest) {
        return PaymentEntity.builder()
                .basketId(basketId)
                .accountId(createPaymentRequest.fromAccountId())
                .amount(createPaymentRequest.paymentAmount().getNumber().numberValue(BigDecimal.class))
                .currency(createPaymentRequest.paymentAmount().getCurrency().getCurrencyCode())
                .recipientIBAN(createPaymentRequest.recipientIban())
                .messageToSelf(createPaymentRequest.messageToSelf())
                .messageToRecipient(createPaymentRequest.messageToRecipient())
                .scheduledDateTime(createPaymentRequest.scheduledDateTime())
                .build();
    }
}
