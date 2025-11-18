package com.alphbank.payment.service.repository.model;

import com.alphbank.payment.service.model.Payment;
import lombok.Builder;
import lombok.Data;
import org.javamoney.moneta.Money;
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

    @Column("basket_id")
    private UUID basketId;

    @Column("core_reference")
    private UUID coreReference;

    @Column("from_account_iban")
    private String fromAccountIban;

    @Column("recipient_iban")
    private String recipientIban;

    @Column("message_to_self")
    private String messageToSelf;

    @Column("message_to_recipient")
    private String messageToRecipient;

    @Column("payment_amount")
    private BigDecimal amount;

    @Column("payment_currency")
    private String currency;

    @Column("recipient_name")
    private String recipientName;

    @Column("scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    @Column("psu_ip_address")
    private String psuIPAddress;

    public static PaymentEntity from(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.id())
                .basketId(payment.basketId())
                .coreReference(payment.coreReference())
                .fromAccountIban(payment.fromAccountIban())
                .recipientIban(payment.recipientIban())
                .messageToSelf(payment.messageToSelf())
                .messageToRecipient(payment.messageToRecipient())
                .amount(payment.monetaryAmount().getNumber().numberValue(BigDecimal.class))
                .currency(payment.monetaryAmount().getCurrency().getCurrencyCode())
                .recipientName(payment.recipientName())
                .scheduledDateTime(payment.scheduledDateTime())
                .psuIPAddress(payment.psuIPAddress())
                .build();
    }

    public Payment toModel(){
        return Payment.builder()
                .id(id)
                .basketId(basketId)
                .coreReference(coreReference)
                .fromAccountIban(fromAccountIban)
                .recipientIban(recipientIban)
                .messageToSelf(messageToSelf)
                .messageToRecipient(messageToRecipient)
                .monetaryAmount(Money.of(amount, currency))
                .recipientName(recipientName)
                .scheduledDateTime(scheduledDateTime)
                .psuIPAddress(psuIPAddress)
                .build();
    }

    public String asFormattedDocument(String documentTemplate) {
        return documentTemplate.formatted(amount, currency, recipientIban, scheduledDateTime);
    }
}
