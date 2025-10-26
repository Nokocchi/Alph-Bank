package com.alphbank.core.customer.service.repository.model;

import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
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

    @Column("national_id")
    private String nationalId;

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
                .nationalId(createCustomerRequest.nationalId())
                .firstName(createCustomerRequest.firstName())
                .lastName(createCustomerRequest.lastName())
                .country(createCustomerRequest.locale().getCountry())
                .language(createCustomerRequest.locale().getLanguage())
                .build();
    }
}
