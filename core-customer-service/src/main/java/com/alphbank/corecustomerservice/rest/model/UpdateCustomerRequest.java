package com.alphbank.corecustomerservice.rest.model;

public record UpdateCustomerRequest (Address address, String phoneNumber, String firstName, String lastName) {

}
