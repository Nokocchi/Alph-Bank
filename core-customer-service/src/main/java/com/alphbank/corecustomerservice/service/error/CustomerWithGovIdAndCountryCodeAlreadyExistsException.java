package com.alphbank.corecustomerservice.service.error;

import com.alphbank.corecustomerservice.rest.model.CreateCustomerRequest;

public class CustomerWithGovIdAndCountryCodeAlreadyExistsException extends RuntimeException {

    public static String formatErrorString(CreateCustomerRequest request){
        return String.format("Customer with government ID %s and country code %s already exists", request.governmentId(), request.locale().getCountry());
    }

    public CustomerWithGovIdAndCountryCodeAlreadyExistsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
