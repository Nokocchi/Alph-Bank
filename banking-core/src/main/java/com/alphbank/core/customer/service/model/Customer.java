package com.alphbank.core.customer.service.model;

import com.alphbank.core.customer.service.repository.model.AddressEntity;
import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import com.alphbank.core.rest.model.CreateCustomerRequestDTO;
import com.alphbank.core.rest.model.CustomerDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;
import java.util.UUID;

@Getter
@Builder
public class Customer {

    private UUID id;

    @NotNull
    private String nationalId;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @NotNull
    private Locale locale;

    private Address address;

    public static Customer from(CreateCustomerRequestDTO createCustomerRequestDTO) {
        return Customer.builder()
                .nationalId(createCustomerRequestDTO.getNationalId())
                .firstName(createCustomerRequestDTO.getFirstName())
                .lastName(createCustomerRequestDTO.getLastName())
                .locale(Locale.of(createCustomerRequestDTO.getLanguage(), createCustomerRequestDTO.getCountryCode()))
                .address(Address.from(createCustomerRequestDTO.getAddress()))
                .build();
    }

    public static Customer from(CustomerEntity customerEntity, AddressEntity addressEntity) {
        return Customer.builder()
                .id(customerEntity.getId())
                .nationalId(customerEntity.getNationalId())
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .locale(Locale.of(customerEntity.getLanguage(), customerEntity.getCountry()))
                .address(addressEntity.toModel())
                .build();
    }

    public CustomerDTO toDTO() {
        return CustomerDTO.builder()
                .id(id)
                .nationalId(nationalId)
                .firstName(firstName)
                .lastName(lastName)
                .language(locale.getLanguage())
                .countryCode(locale.getCountry())
                .address(address != null ? address.toDTO() : null)
                .build();
    }
}
