package com.alphbank.corecustomerservice.service.repository.model;

import com.alphbank.corecustomerservice.rest.model.Address;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

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

    public static AddressEntity from(UUID customerId, Address address){
        return AddressEntity.builder()
                .customerId(customerId)
                .streetAddress(address.streetAddress())
                .city(address.city())
                .country(address.country())
                .build();
    }
}
