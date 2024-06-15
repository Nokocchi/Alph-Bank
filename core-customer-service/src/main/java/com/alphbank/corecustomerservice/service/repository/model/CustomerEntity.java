package com.alphbank.corecustomerservice.service.repository.model;

import com.alphbank.corecustomerservice.rest.model.CreateCustomerRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@Table("customer")
public class CustomerEntity {

    @Column("id")
    @Id
    private UUID customerId;

    @Column("government_id")
    private String governmentId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("phone_number")
    private String phoneNumber;

    @Column("customer_language")
    private String language;

    @Column("country")
    private String country;

    public static CustomerEntity from(CreateCustomerRequest createCustomerRequest) {
        return CustomerEntity.builder()
                .governmentId(createCustomerRequest.governmentId())
                .firstName(createCustomerRequest.firstName())
                .lastName(createCustomerRequest.lastName())
                .country(createCustomerRequest.locale().getCountry())
                .language(createCustomerRequest.locale().getLanguage())
                .build();
    }
}
