package com.alphbank.core.unit.service;

import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.customer.service.model.Address;
import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.payment.service.model.Payment;
import org.javamoney.moneta.Money;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

public class UnitTestBase {

    protected static String NATIONAL_ID_1 = "12345";
    protected static String NATIONAL_ID_2 = "56789";
    protected final String MESSAGE_TO_SELF = "From me";
    protected final String MESSAGE_TO_RECIPIENT = "To you";
    protected final String IBAN_1 = "SE2711100000000000893383";
    protected final String IBAN_2 = "FR7612345987650123456789014";


    protected Payment createPayment(UUID fromAccountId, String recipientIban, LocalDateTime scheduledDateTime) {
        return Payment.builder()
                .fromAccountId(fromAccountId)
                .recipientIban(recipientIban)
                .recipientName("Mr. Alph")
                .amount(Money.of(61, "SEK"))
                .scheduledDateTime(scheduledDateTime)
                .messageToSelf(MESSAGE_TO_SELF)
                .messageToRecipient(MESSAGE_TO_RECIPIENT)
                .build();
    }

    protected Customer createCustomer(String nationalId) {
        return createCustomer(UUID.randomUUID(), "SE", nationalId);
    }

    protected Customer createCustomer(UUID customerId, String countryCode, String nationalId) {
        return Customer.builder()
                .id(customerId)
                .nationalId(nationalId)
                .locale(Locale.of("sv", countryCode))
                .firstName("John")
                .lastName("Doe")
                .address(Address.builder()
                        .streetAddress("Street")
                        .city("City")
                        .country("Country")
                        .build())
                .build();
    }

    protected Account createAccount(String iban) {
        return createAccount(iban, UUID.randomUUID());
    }

    protected Account createAccount(String iban, UUID customerId) {
        return Account.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .accountName("AccountName")
                .iban(iban)
                .balance(Money.of(0, "SEK"))
                .build();
    }
}
