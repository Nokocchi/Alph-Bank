package com.alphbank.core.payment.service.repository.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.payment.service.model.PeriodicPayment;
import com.alphbank.core.rest.model.PeriodicPaymentFreuqencyDTO;
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
@Table("periodic_payment")
public class PeriodicPaymentEntity {

    @Column("id")
    @Id
    private UUID id;

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

    @Column("frequency")
    private PeriodicPaymentFreuqencyDTO frequency;

    @Builder.Default
    @Column("active")
    private boolean active = false;

    @Column("start_date")
    private LocalDateTime startDate;

    @With
    @Column("end_date")
    private LocalDateTime endDate;

    public static PeriodicPaymentEntity from(PeriodicPayment periodicPayment) {
        return PeriodicPaymentEntity.builder()
                .fromAccountId(periodicPayment.getFromAccountId())
                .monetaryValue(Utils.getAmount(periodicPayment.getAmount()))
                .currency(Utils.getCurrencyCode(periodicPayment.getAmount()))
                .recipientIban(periodicPayment.getRecipientIban())
                .recipientAccountId(periodicPayment.getRecipientAccountId())
                .recipientName(periodicPayment.getRecipientName())
                .messageToSelf(periodicPayment.getMessageToSelf())
                .messageToRecipient(periodicPayment.getMessageToRecipient())
                .frequency(periodicPayment.getFrequency())
                .startDate(periodicPayment.getStartDate())
                .endDate(periodicPayment.getEndDate())
                .build();
    }


}
