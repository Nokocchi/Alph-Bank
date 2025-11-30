package com.alphbank.core.customer.service.repository.model;

import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.rest.model.UpdateCustomerRequestDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Locale;
import java.util.UUID;

@Builder
@Data
@Table("customer")
public class CustomerEntity {

    @Column("id")
    @Id
    private UUID id;

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

    public static CustomerEntity from(Customer customer) {
        return CustomerEntity.builder()
                .nationalId(customer.getNationalId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .country(customer.getLocale().getCountry())
                .language(customer.getLocale().getLanguage())
                .build();
    }

    public Customer toModel() {
        return Customer.builder()
                .id(id)
                .nationalId(nationalId)
                .firstName(firstName)
                .lastName(lastName)
                .locale(Locale.of(language, country))
                .build();
    }

    public CustomerEntity applyUpdate(UpdateCustomerRequestDTO update) {
        phoneNumber = update.getPhoneNumber();
        firstName = update.getFirstName();
        lastName = update.getLastName();
        return this;
    }
}
