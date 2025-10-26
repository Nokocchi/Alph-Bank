package com.alphbank.core.customer.rest.model;

public record UpdateCustomerRequest (Address address, String phoneNumber, String firstName, String lastName) {

}
