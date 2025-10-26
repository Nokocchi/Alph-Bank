package com.alphbank.core.customer.service.error;

public class DuplicateCustomerException extends RuntimeException {

    public DuplicateCustomerException(String nationalId, String country, Throwable cause) {
        super(String.format("Customer with national identity number %s and country code %s already exists", nationalId, country), cause);
    }
}
