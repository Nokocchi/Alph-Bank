package com.alphbank.core.customer.service.repository.model;

import com.alphbank.core.customer.service.model.Address;
import com.alphbank.core.rest.model.AddressDTO;
import com.alphbank.core.rest.model.UpdateCustomerRequestDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Builder
@Data
@Table("address")
public class AddressEntity {

    @Column("id")
    @Id
    private UUID addressId;

    @Column("customer_id")
    private UUID customerId;

    @Column("street_address")
    private String streetAddress;

    @Column("city")
    private String city;

    @Column("country")
    private String country;

    public static AddressEntity from(UUID customerId, Address address) {
        return AddressEntity.builder()
                .customerId(customerId)
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .country(address.getCountry())
                .build();
    }

    public Address toModel() {
        return Address.builder()
                .streetAddress(streetAddress)
                .city(city)
                .country(country)
                .build();
    }

    public AddressEntity applyUpdate(UpdateCustomerRequestDTO updateCustomerRequestDTO) {
        // TODO: Maybe just send the entire, fully updated address every time and set it without checks?

        AddressDTO address = updateCustomerRequestDTO.getAddress();
        if (address == null) {
            return this;
        }

        if (address.getStreetAddress() != null) {
            streetAddress = address.getStreetAddress();
        }

        if (address.getCity() != null) {
            city = address.getCity();
        }

        if (address.getCountry() != null) {
            country = address.getCountry();
        }

        return this;
    }
}
