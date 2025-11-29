package com.alphbank.core.account.service.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.rest.model.AccountDTO;
import com.alphbank.core.rest.model.CreateAccountRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import com.alphbank.core.rest.model.TransactionDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Transaction {

    private UUID id;

    @NotNull
    private String currencyCode;

    @NotNull
    private MonetaryAmount amount;

    @NotNull
    private MonetaryAmount newBalance;

    private String message;

    @NotNull
    private LocalDateTime executedDateTime;

    public TransactionDTO toDTO(){
        return TransactionDTO.builder()
                .id(id)
                .currencyCode(currencyCode)
                .amount(MonetaryAmountDTO.builder()
                        .amount(Utils.getAmount(amount))
                        .currency(Utils.getCurrencyCode(amount))
                        .build())
                .newBalance(MonetaryAmountDTO.builder()
                        .amount(Utils.getAmount(newBalance))
                        .currency(Utils.getCurrencyCode(newBalance))
                        .build())
                .message(message)
                .executedDateTime(executedDateTime)
                .build();
    }
}
