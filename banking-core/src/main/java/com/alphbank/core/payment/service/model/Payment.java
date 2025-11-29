package com.alphbank.core.payment.service.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import com.alphbank.core.rest.model.PaymentSearchResultDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Payment {

    private UUID id;

    @NotNull
    private UUID fromAccountId;

    private MonetaryAmount amount;

    private UUID recipientAccountId;

    private String recipientIban;

    private String messageToSelf;

    private String messageToRecipient;

    private LocalDateTime executedDateTime;

    private LocalDateTime scheduledDateTime;

    public PaymentDTO toDTO(){
        return PaymentDTO.builder()
                .id(id)
                .fromAccountId(fromAccountId)
                .amount(MonetaryAmountDTO.builder()
                        .amount(Utils.getAmount(amount))
                        .currency(Utils.getCurrencyCode(amount))
                        .build())
                .recipientIban(recipientIban)
                .messageToSelf(messageToSelf)
                .messageToRecipient(messageToRecipient)
                .executedDateTime(executedDateTime)
                .scheduledDateTime(scheduledDateTime)
                .build();
    }

    public static Payment from(CreatePaymentRequestDTO createPaymentRequestDTO) {
        return Payment.builder()
                .fromAccountId(createPaymentRequestDTO.getFromAccountId())
                .amount(Money.of(createPaymentRequestDTO.getAmount().getAmount(), createPaymentRequestDTO.getAmount().getCurrency()))
                .recipientIban(createPaymentRequestDTO.getRecipientIban())
                .recipientAccountId(createPaymentRequestDTO.getRecipientAccountId())
                .messageToSelf(createPaymentRequestDTO.getMessageToSelf())
                .messageToRecipient(createPaymentRequestDTO.getMessageToRecipient())
                .scheduledDateTime(createPaymentRequestDTO.getScheduledDateTime())
                .build();
    }

    // TODO: I probably need some more fields here, like from/to IBAN, but I will do that later.
    public static Payment fromEntity(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(paymentEntity.getId())
                .fromAccountId(paymentEntity.getFromAccountId())
                .messageToSelf(paymentEntity.getMessageToSelf())
                .messageToRecipient(paymentEntity.getMessageToRecipient())
                .recipientAccountId(paymentEntity.getRecipientAccountId())
                .amount(Money.of(paymentEntity.getMonetaryValue(), paymentEntity.getCurrency()))
                .scheduledDateTime(paymentEntity.getScheduledDateTime())
                .executedDateTime(paymentEntity.getExecutionDateTime())
                .build();
    }

}