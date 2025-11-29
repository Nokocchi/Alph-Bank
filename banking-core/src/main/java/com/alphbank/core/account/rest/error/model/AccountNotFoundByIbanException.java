package com.alphbank.core.account.rest.error.model;

public class AccountNotFoundByIbanException extends RuntimeException {

    public AccountNotFoundByIbanException(String iban) {
        super(String.format("Account with iban %s not found", iban));
    }
}
