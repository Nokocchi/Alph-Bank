package com.alphbank.core.customer.rest.error.model;

import java.util.UUID;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(UUID customerId) {
        super("Address with customerId: %s not found".formatted(customerId));
    }
}
