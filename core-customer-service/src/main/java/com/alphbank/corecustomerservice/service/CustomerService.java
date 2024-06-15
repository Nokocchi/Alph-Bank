package com.alphbank.corecustomerservice.service;

import com.alphbank.corecustomerservice.rest.model.Address;
import com.alphbank.corecustomerservice.rest.model.CreateCustomerRequest;
import com.alphbank.corecustomerservice.rest.model.Customer;
import com.alphbank.corecustomerservice.rest.model.UpdateCustomerRequest;
import com.alphbank.corecustomerservice.service.error.CustomerNotFoundException;
import com.alphbank.corecustomerservice.service.error.CustomerWithGovIdAndCountryCodeAlreadyExistsException;
import com.alphbank.corecustomerservice.service.repository.AddressRepository;
import com.alphbank.corecustomerservice.service.repository.CustomerRepository;
import com.alphbank.corecustomerservice.service.repository.model.AddressEntity;
import com.alphbank.corecustomerservice.service.repository.model.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public Flux<Customer> findAllCustomersByGovernmentId(String governmentId) {
        return customerRepository.findAllByGovernmentId(governmentId)
                .flatMap(this::findAddressAndConvertToRestModel);
    }

    public Mono<Customer> createCustomer(CreateCustomerRequest createCustomerRequest) {
        CustomerEntity customerEntity = CustomerEntity.from(createCustomerRequest);
        return customerRepository.save(customerEntity)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    String errorMessage = CustomerWithGovIdAndCountryCodeAlreadyExistsException.formatErrorString(createCustomerRequest);
                    throw new CustomerWithGovIdAndCountryCodeAlreadyExistsException(errorMessage, e);
                })
                .flatMap(persistedCustomerEntity -> createAddress(persistedCustomerEntity.getCustomerId(), createCustomerRequest.address()))
                .map(addressEntity -> convertToRestModel(customerEntity, addressEntity));
    }

    private Mono<AddressEntity> createAddress(UUID customerId, Address address) {
        AddressEntity addressEntity = AddressEntity.from(customerId, address);
        return addressRepository.save(addressEntity);
    }

    public Mono<Customer> getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException()))
                .flatMap(this::findAddressAndConvertToRestModel);
    }

    public Mono<Void> deleteCustomer(UUID customerId) {
        return customerRepository.deleteById(customerId);
    }

    private Mono<Customer> findAddressAndConvertToRestModel(CustomerEntity customerEntity) {
        return addressRepository.findByCustomerId(customerEntity.getCustomerId())
                .map(addressEntity -> convertToRestModel(customerEntity, addressEntity))
                .switchIfEmpty(Mono.just(convertToRestModel(customerEntity)));
    }

    private Customer convertToRestModel(CustomerEntity customerEntity, AddressEntity addressEntity) {
        Customer customer = convertToRestModel(customerEntity);
        customer.setAddress(convertToRestModel(addressEntity));
        return customer;
    }

    private Customer convertToRestModel(CustomerEntity customerEntity) {
        return new Customer(customerEntity.getCustomerId(),
                null,
                customerEntity.getPhoneNumber(),
                customerEntity.getGovernmentId(),
                customerEntity.getFirstName(),
                customerEntity.getLastName(),
                new Locale(customerEntity.getLanguage(), customerEntity.getCountry()));
    }

    private Address convertToRestModel(AddressEntity addressEntity) {
        return new Address(addressEntity.getStreetAddress(),
                addressEntity.getCity(),
                addressEntity.getCountry());
    }

    public Mono<Customer> updateCustomer(UUID customerId, UpdateCustomerRequest updateCustomerRequest) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException()))
                .map(entity -> updateEntity(entity, updateCustomerRequest))
                .flatMap(customerRepository::save)
                .zipWith(updateAndPersistAddress(customerId, updateCustomerRequest.address()))
                .map(tuple2 -> convertToRestModel(tuple2.getT1(), tuple2.getT2()));
    }

    private Mono<AddressEntity> updateAndPersistAddress(UUID customerId, Address newAddress) {
        return Mono.just(newAddress)
                .filter(Objects::nonNull)
                .flatMap(newAddressNotNull -> addressRepository.findByCustomerId(customerId))
                .map(entity -> updateAddressEntity(entity, newAddress));
    }

    private AddressEntity updateAddressEntity(AddressEntity addressEntity, Address newAddress) {
        if (newAddress.streetAddress() != null) {
            addressEntity.setStreetAddress(newAddress.streetAddress());
        }

        if (newAddress.city() != null) {
            addressEntity.setCity(newAddress.city());
        }

        if (newAddress.country() != null) {
            addressEntity.setCountry(newAddress.country());
        }

        return addressEntity;
    }

    private CustomerEntity updateEntity(CustomerEntity entity, UpdateCustomerRequest newCustomerData) {
        if (newCustomerData.phoneNumber() != null) {
            entity.setPhoneNumber(newCustomerData.phoneNumber());
        }

        if (newCustomerData.firstName() != null) {
            entity.setFirstName(newCustomerData.firstName());
        }

        if (newCustomerData.lastName() != null) {
            entity.setLastName(newCustomerData.lastName());
        }

        return entity;
    }
}
