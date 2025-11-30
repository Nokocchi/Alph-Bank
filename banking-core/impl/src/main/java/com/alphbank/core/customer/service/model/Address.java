package com.alphbank.core.customer.service.model;

import com.alphbank.core.rest.model.AddressDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class Address {

    private UUID id;

    @NotNull
    private String streetAddress;

    @NotNull
    private String city;

    @NotNull
    private String country;

    public static Address from(AddressDTO addressDTO){
        return Address.builder()
                .streetAddress(addressDTO.getStreetAddress())
                .city(addressDTO.getCity())
                .country(addressDTO.getCountry())
                .build();
    }

    public AddressDTO toDTO() {
        return AddressDTO.builder()
                .streetAddress(streetAddress)
                .city(city)
                .country(country)
                .build();
    }
}
