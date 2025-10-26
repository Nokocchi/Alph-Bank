package com.alphbank.core.customer.rest.model;

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
    private String nationalId;
    private String firstName;
    private String lastName;
    private Locale locale;

}
