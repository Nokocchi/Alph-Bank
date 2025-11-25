package com.alphbank.core.account.service.model;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.rest.model.AccountDTO;
import com.alphbank.core.rest.model.CreateAccountRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import javax.money.MonetaryAmount;
import java.util.UUID;

@Getter
@Builder
public class Account {

    private UUID id;

    @NotNull
    private UUID customerId;

    private String accountName;

    private MonetaryAmount balance;

    private String iban;

    public static Account from(CreateAccountRequestDTO createAccountRequestDTO) {
        return Account.builder()
                .customerId(createAccountRequestDTO.getCustomerId())
                .accountName(createAccountRequestDTO.getAccountName())
                .build();
    }

    public AccountDTO toDTO() {
        return AccountDTO.builder()
                .id(id)
                .accountName(accountName)
                .balance(MonetaryAmountDTO.builder()
                        .currency(Utils.getCurrencyCode(balance))
                        .amount(Utils.getAmount(balance))
                        .build())
                .iban(iban)
                .build();
    }
}
