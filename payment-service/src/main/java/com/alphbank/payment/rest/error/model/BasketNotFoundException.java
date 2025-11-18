package com.alphbank.payment.rest.error.model;

import java.util.UUID;

public class BasketNotFoundException extends RuntimeException {
    public BasketNotFoundException(UUID basketId) {
        super("Basket with id %s not found".formatted(basketId));
    }
}
