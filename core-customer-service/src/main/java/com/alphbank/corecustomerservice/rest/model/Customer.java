package com.alphbank.corecustomerservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Customer {

    private UUID id;
    private Address address;
    private String phoneNumber;
    private String governmentId;
    private String firstName;
    private String lastName;
    private Locale locale;

}
