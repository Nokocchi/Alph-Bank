package com.alphbank.core.payment.service.repository.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.payment.service.model.Payment;
import jakarta.validation.constraints.NotNull;
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
    private UUID id;

    @Column("periodic_payment_id")
    private UUID periodicPaymentId;

    @Column("from_account_id")
    private UUID fromAccountId;

    @Column("monetary_value")
    private BigDecimal monetaryValue;

    @Column("currency")
    private String currency;

    @Column("recipient_iban")
    private String recipientIban;

    @Column("recipient_name")
    private String recipientName;

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


    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .fromAccountId(payment.getFromAccountId())
                .messageToSelf(payment.getMessageToSelf())
                .messageToRecipient(payment.getMessageToRecipient())
                .recipientIban(payment.getRecipientIban())
                .recipientAccountId(payment.getRecipientAccountId())
                .recipientName(payment.getRecipientName())
                .monetaryValue(Utils.getAmount(payment.getAmount()))
                .currency(Utils.getCurrencyCode(payment.getAmount()))
                .scheduledDateTime(payment.getScheduledDateTime())
                .build();
    }


    public static PaymentEntity createFrom(PeriodicPaymentEntity periodicPaymentEntity, LocalDateTime scheduledDateTime) {
        return PaymentEntity.builder()
                .fromAccountId(periodicPaymentEntity.getFromAccountId())
                .periodicPaymentId(periodicPaymentEntity.getId())
                .messageToSelf(periodicPaymentEntity.getMessageToSelf())
                .messageToRecipient(periodicPaymentEntity.getMessageToRecipient())
                .recipientIban(periodicPaymentEntity.getRecipientIban())
                .recipientAccountId(periodicPaymentEntity.getRecipientAccountId())
                .recipientName(periodicPaymentEntity.getRecipientName())
                .monetaryValue(periodicPaymentEntity.getMonetaryValue())
                .currency(periodicPaymentEntity.getCurrency())
                .scheduledDateTime(scheduledDateTime)
                .build();
    }
}
