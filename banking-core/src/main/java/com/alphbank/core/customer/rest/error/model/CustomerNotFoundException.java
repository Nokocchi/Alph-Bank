package com.alphbank.core.customer.rest.error.model;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer with customerId: %s not found".formatted(customerId));
    }
}
