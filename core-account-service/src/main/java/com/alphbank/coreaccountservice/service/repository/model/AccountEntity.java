package com.alphbank.coreaccountservice.service.repository.model;

import com.alphbank.coreaccountservice.rest.model.CreateAccountRequest;
import lombok.Builder;
import lombok.Data;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Random;
import java.util.UUID;

@Builder
@Data
@Table("account")
public class AccountEntity {

    @Column("id")
    @Id
    private UUID accountId;

    @Column("customer_id")
    private UUID customerId;

    @Column("currency_code")
    private String currencyCode;

    @Column("balance")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column("iban")
    private String iban;

    public static AccountEntity from(CreateAccountRequest createAccountRequest) {
        return AccountEntity.builder()
                .customerId(createAccountRequest.customerId())
                .iban(new Iban.Builder()
                        .countryCode(CountryCode.getByCode(createAccountRequest.locale().getCountry()))
                        .bankCode("111")
                        .leftPadding(true)
                        .paddingCharacter('0')
                        .accountNumber("" + new Random().nextInt(100_000, 1_000_000))
                        .build()
                        .toString())
                .currencyCode(Currency.getInstance(createAccountRequest.locale()).getCurrencyCode())
                .build();
    }
}
