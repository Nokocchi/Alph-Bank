package com.alphbank.payment.service.model;

import com.alphbank.payment.rest.model.request.CreatePaymentRequest;
import com.alphbank.payment.service.amqp.model.SigningStatus;
import com.alphbank.payment.service.repository.model.PaymentEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import org.berlingroup.rest.model.*;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Builder
@Setter
@Getter
@Accessors(fluent = true)
public class Payment {

    private UUID id;
    private UUID basketId;
    private UUID coreReference;

    @NotNull
    private final String fromAccountIban;

    @NotNull
    private final String recipientIban;

    @NotNull
    private final String messageToSelf;

    @NotNull
    private final String messageToRecipient;

    @NotNull
    private final MonetaryAmount monetaryAmount;

    @With
    private PaymentTransactionStatus transactionStatus;

    private final String recipientName;

    private LocalDateTime scheduledDateTime;

    private UUID requestId;

    private String psuIPAddress;

    public static Payment fromDTO(PaymentInitiationJsonDTO dto, String psuIPAddress, UUID requestId) {
        return Payment.builder()
                .fromAccountIban(dto.getDebtorAccount().getIban())
                .recipientIban(dto.getCreditorAccount().getIban())
                .messageToSelf((String) dto.getRemittanceInformationUnstructured())
                .messageToRecipient((String) dto.getRemittanceInformationUnstructured())
                .monetaryAmount(Money.of(
                        new BigDecimal(dto.getInstructedAmount().getAmount()),
                        dto.getInstructedAmount().getCurrency()
                ))
                .recipientName((String) dto.getCreditorName())
                .psuIPAddress(psuIPAddress)
                .requestId(requestId)
                .build();
    }

    public static Payment fromDTO(CreatePaymentRequest dto) {
        return Payment.builder()
                .fromAccountIban(dto.fromAccountIban())
                .recipientIban(dto.recipientIban())
                .monetaryAmount(Money.of(
                        dto.amount().getNumber().numberValue(BigDecimal.class),
                        dto.amount().getCurrency().getCurrencyCode()
                ))
                .messageToSelf(dto.messageToSelf())
                .messageToRecipient(dto.messageToRecipient())
                .build();
    }

    public PaymentInitiationRequestResponse201DTO toPSD2InitiationDTO() {
        return PaymentInitiationRequestResponse201DTO.builder()
                .paymentId(id.toString())
                .transactionStatus(TransactionStatusDTO.RCVD) // Payments are always initiated with the RECEIVED status
                .links(new LinksPaymentInitiationDTO()) // HATEOAS is not in scope
                .build();
    }

    public PaymentInitiationWithStatusResponseDTO toPSD2InfoDTO() {
        return PaymentInitiationWithStatusResponseDTO.builder()
                .debtorAccount(AccountReferenceDTO.builder()
                        .iban(fromAccountIban)
                        .build())
                .creditorAccount(AccountReferenceDTO.builder()
                        .iban(recipientIban)
                        .build())
                .instructedAmount(AmountDTO.builder()
                        .amount(monetaryAmount.getNumber().numberValue(BigDecimal.class).toPlainString())
                        .build())
                .creditorName(recipientName)
                .remittanceInformationUnstructured(messageToRecipient)
                .transactionStatus(Optional.ofNullable(transactionStatus)
                        .map(PaymentTransactionStatus::toPSD2TransactionStatus)
                        .orElse(null))
                .build();
    }

    public PaymentInitiationStatusResponse200JsonDTO toPSD2StatusDTO() {
        return PaymentInitiationStatusResponse200JsonDTO.builder()
                .transactionStatus(transactionStatus.toPSD2TransactionStatus())
                .build();
    }

}
