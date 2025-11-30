package com.alphbank.core.unit.service.customer;

import com.alphbank.core.customer.service.model.Address;
import com.alphbank.core.customer.service.model.Customer;

import java.util.Locale;
import java.util.UUID;

public class CustomerUnitTestBase {

    protected static String NATIONAL_ID_1 = "12345";
    protected static String NATIONAL_ID_2 = "56789";

    protected Customer createCustomer(String nationalId) {
        return createCustomer(null, "SE", nationalId);
    }

    protected Customer createCustomer(UUID customerId, String countryCode, String nationalId) {
        return Customer.builder()
                .id(customerId)
                .nationalId(nationalId)
                .locale(Locale.of("sv", countryCode))
                .firstName("John")
                .lastName("Doe")
                .address(Address.builder()
                        .streetAddress("Street")
                        .city("City")
                        .country("Country")
                        .build())
                .build();
    }
}
