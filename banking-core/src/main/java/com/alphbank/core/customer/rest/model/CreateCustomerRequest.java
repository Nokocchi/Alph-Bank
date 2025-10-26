package com.alphbank.core.customer.rest.model;

import java.util.Locale;

public record CreateCustomerRequest(Locale locale, String nationalId, String firstName, String lastName, Address address) {
}
