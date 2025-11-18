package com.alphbank.payment.rest.error.model;

import java.util.UUID;

public class CannotDeleteBasketWithAuthorizationException extends RuntimeException {
    public CannotDeleteBasketWithAuthorizationException(UUID basketId) {
        super("Basket with id %s cannot be deleted because an authorization has been started".formatted(basketId));
    }
}
