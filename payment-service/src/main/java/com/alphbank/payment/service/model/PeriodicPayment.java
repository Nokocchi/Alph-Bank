package com.alphbank.payment.service.model;

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
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Builder
@Setter
@Getter
@Accessors(fluent = true)
public class PeriodicPayment {

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
    private PeriodicPaymentTransactionStatus transactionStatus;

    private String recipientName;

    private LocalDate startDate;

    private LocalDate endDate;

    private PeriodicPaymentFrequency frequency;

    private String psuIPAddress;

    public static PeriodicPayment fromDTO(PeriodicPaymentInitiationJsonDTO dto, String psuIPAddress) {
        return PeriodicPayment.builder()
                .fromAccountIban(dto.getDebtorAccount().getIban())
                .recipientIban(dto.getCreditorAccount().getIban())
                .messageToSelf((String) dto.getRemittanceInformationUnstructured())
                .messageToRecipient((String) dto.getRemittanceInformationUnstructured())
                .monetaryAmount(Money.of(
                        new BigDecimal(dto.getInstructedAmount().getAmount()),
                        dto.getInstructedAmount().getCurrency()
                ))
                .recipientName((String) dto.getCreditorName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .frequency(PeriodicPaymentFrequency.from(dto.getFrequency()))
                .psuIPAddress(psuIPAddress)
                .build();
    }

    public PaymentInitiationRequestResponse201DTO toPSD2InitiationDTO() {
        return PaymentInitiationRequestResponse201DTO.builder()
                .paymentId(id.toString())
                .transactionStatus(TransactionStatusDTO.RCVD) // Payments are always initiated with the RECEIVED status
                .links(new LinksPaymentInitiationDTO()) // HATEOAS is not in scope
                .build();
    }

    public PeriodicPaymentInitiationWithStatusResponseDTO toPSD2InfoDTO() {
        return PeriodicPaymentInitiationWithStatusResponseDTO.builder()
                .debtorAccount(AccountReferenceDTO.builder()
                        .iban(fromAccountIban)
                        .build())
                .creditorAccount(AccountReferenceDTO.builder()
                        .iban(recipientIban)
                        .build())
                .remittanceInformationUnstructured(messageToRecipient)
                .instructedAmount(AmountDTO.builder()
                        .amount(monetaryAmount.getNumber().numberValue(BigDecimal.class).toPlainString())
                        .build())
                .transactionStatus(Optional.ofNullable(transactionStatus)
                        .map(PeriodicPaymentTransactionStatus::toPSD2TransactionStatus)
                        .orElse(null))
                .creditorName(recipientName)
                .startDate(startDate)
                .endDate(endDate)
                .frequency(frequency.toPSD2Frequency())
                .build();
    }

    public PaymentInitiationStatusResponse200JsonDTO toPSD2StatusDTO() {
        return PaymentInitiationStatusResponse200JsonDTO.builder()
                .transactionStatus(transactionStatus.toPSD2TransactionStatus())
                .build();
    }

}
