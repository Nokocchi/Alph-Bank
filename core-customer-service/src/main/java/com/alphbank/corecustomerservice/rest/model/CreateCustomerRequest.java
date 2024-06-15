package com.alphbank.corecustomerservice.rest.model;

import java.util.Locale;

public record CreateCustomerRequest(Locale locale, String governmentId, String firstName, String lastName, Address address) {
}
