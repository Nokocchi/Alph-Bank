package com.alphbank.payment.service.repository.model;

import com.alphbank.payment.service.model.Payment;
import com.alphbank.payment.service.model.PeriodicPayment;
import com.alphbank.payment.service.model.PeriodicPaymentFrequency;
import lombok.Builder;
import lombok.Data;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@Table("periodic_payment")
public class PeriodicPaymentEntity {

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

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("frequency")
    private PeriodicPaymentFrequency frequency;

    @Column("psu_ip_address")
    private String psuIPAddress;

    //executionRule
    //dayOfExecution
    //monthsOfExecution

    public static PeriodicPaymentEntity from(PeriodicPayment payment) {
        return PeriodicPaymentEntity.builder()
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
                .startDate(payment.startDate())
                .endDate(payment.endDate())
                .frequency(payment.frequency())
                .psuIPAddress(payment.psuIPAddress())
                .build();
    }

    public PeriodicPayment toModel(){
        return PeriodicPayment.builder()
                .id(id)
                .basketId(basketId)
                .coreReference(coreReference)
                .fromAccountIban(fromAccountIban)
                .recipientIban(recipientIban)
                .messageToSelf(messageToSelf)
                .messageToRecipient(messageToRecipient)
                .monetaryAmount(Money.of(amount, currency))
                .recipientName(recipientName)
                .startDate(startDate)
                .endDate(endDate)
                .frequency(frequency)
                .psuIPAddress(psuIPAddress)
                .build();
    }

    public String asFormattedDocument(String documentTemplate) {
        return documentTemplate.formatted(amount, currency, recipientIban, startDate, endDate, frequency);
    }

}
