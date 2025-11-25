package com.alphbank.core.account.service.repository.model;

import com.alphbank.core.account.service.model.Account;
import lombok.Builder;
import lombok.Data;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Builder
@Data
@Table("account")
public class AccountEntity {

    private static final String BANK_CODE = "111";

    @Column("id")
    @Id
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("account_name")
    private String accountName;

    @Column("currency_code")
    private String currencyCode;

    @Column("balance")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column("iban")
    private String iban;

    public static AccountEntity from(Account account, Locale locale) {
        return AccountEntity.builder()
                .customerId(account.getCustomerId())
                .accountName(account.getAccountName())
                .iban(new Iban.Builder()
                        .countryCode(CountryCode.getByCode(locale.getCountry()))
                        .bankCode(BANK_CODE)
                        .leftPadding(true)
                        .paddingCharacter('0')
                        .accountNumber("" + new Random().nextInt(100_000, 1_000_000))
                        .build()
                        .toString())
                .currencyCode(Currency.getInstance(locale).getCurrencyCode())
                .build();
    }

    public Account toModel() {
        return Account.builder()
                .id(id)
                .customerId(customerId)
                .accountName(accountName)
                .balance(Money.of(balance, currencyCode))
                .iban(iban)
                .build();
    }
}
