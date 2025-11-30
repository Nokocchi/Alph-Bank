package com.alphbank.core.payment.service.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.payment.service.repository.model.PeriodicPaymentEntity;
import com.alphbank.core.rest.model.CreatePeriodicPaymentRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import com.alphbank.core.rest.model.PeriodicPaymentDTO;
import com.alphbank.core.rest.model.PeriodicPaymentFreuqencyDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PeriodicPayment {

    private UUID id;

    @NotNull
    private UUID fromAccountId;

    @NotNull
    private MonetaryAmount amount;

    @NotNull
    private String recipientIban;

    @NotNull
    private String recipientName;

    @With
    private UUID recipientAccountId;

    private String messageToSelf;

    private String messageToRecipient;

    @NotNull
    private PeriodicPaymentFreuqencyDTO frequency;

    // TODO: I either need to remove this flag and rely only on start/end date, or have a scheduled job that checks if any spawned payments are waiting and if not then it creates one, or completely rewrite the periodicPayment spawning logic
    private boolean active;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    // TODO: Figure out what I want to return to the frontend
    public PeriodicPaymentDTO toDTO() {
        return PeriodicPaymentDTO.builder()
                .id(id)
                .fromAccountId(fromAccountId)
                .amount(MonetaryAmountDTO.builder()
                        .amount(Utils.getAmount(amount))
                        .currency(Utils.getCurrencyCode(amount))
                        .build())
                .recipientIban(recipientIban)
                .messageToSelf(messageToSelf)
                .messageToRecipient(messageToRecipient)
                .build();
    }

    public static PeriodicPayment from(CreatePeriodicPaymentRequestDTO createPeriodicPaymentRequestDTO) {
        return PeriodicPayment.builder()
                .fromAccountId(createPeriodicPaymentRequestDTO.getFromAccountId())
                .amount(Money.of(createPeriodicPaymentRequestDTO.getAmount().getAmount(), createPeriodicPaymentRequestDTO.getAmount().getCurrency()))
                .recipientIban(createPeriodicPaymentRequestDTO.getRecipientIban())
                .recipientName(createPeriodicPaymentRequestDTO.getRecipientName())
                .messageToSelf(createPeriodicPaymentRequestDTO.getMessageToSelf())
                .messageToRecipient(createPeriodicPaymentRequestDTO.getMessageToRecipient())
                .frequency(createPeriodicPaymentRequestDTO.getFrequency())
                .startDate(createPeriodicPaymentRequestDTO.getStartDate())
                .endDate(createPeriodicPaymentRequestDTO.getEndDate())
                .build();
    }

    public static PeriodicPayment fromEntity(PeriodicPaymentEntity periodicPaymentEntity) {
        return PeriodicPayment.builder()
                .fromAccountId(periodicPaymentEntity.getFromAccountId())
                .amount(Money.of(periodicPaymentEntity.getMonetaryValue(), periodicPaymentEntity.getCurrency()))
                .recipientIban(periodicPaymentEntity.getRecipientIban())
                .messageToSelf(periodicPaymentEntity.getMessageToSelf())
                .messageToRecipient(periodicPaymentEntity.getMessageToRecipient())
                .frequency(periodicPaymentEntity.getFrequency())
                .startDate(periodicPaymentEntity.getStartDate())
                .endDate(periodicPaymentEntity.getEndDate())
                .build();
    }

}