package com.alphbank.core.customer.service;

import com.alphbank.core.customer.rest.error.model.AddressNotFoundException;
import com.alphbank.core.customer.rest.error.model.CustomerNotFoundException;
import com.alphbank.core.customer.rest.error.model.DuplicateCustomerException;
import com.alphbank.core.customer.service.model.Address;
import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.customer.service.repository.model.AddressEntity;
import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import com.alphbank.core.rest.model.UpdateCustomerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public Flux<Customer> findAllCustomers() {
        return customerRepository.findAll()
                .map(CustomerEntity::toModel);
    }

    @Transactional
    public Mono<Customer> createCustomer(Customer customer) {
        return Mono.just(customer)
                .map(CustomerEntity::from)
                .flatMap(customerRepository::save)
                .onErrorMap(DuplicateKeyException.class,
                        e -> new DuplicateCustomerException(customer.getNationalId(), customer.getLocale().getCountry(), e))
                .zipWhen(savedCustomerEntity -> createAddress(savedCustomerEntity.getId(), customer.getAddress()))
                .map(TupleUtils.function(Customer::from));
    }

    private Mono<AddressEntity> createAddress(UUID customerId, Address address) {
        AddressEntity addressEntity = AddressEntity.from(customerId, address);
        return addressRepository.save(addressEntity);
    }

    public Mono<Customer> getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .zipWhen(customerEntity -> addressRepository.findByCustomerId(customerEntity.getId()))
                .map(TupleUtils.function(Customer::from));
    }

    public Mono<Void> deleteCustomer(UUID customerId) {
        return customerRepository.deleteById(customerId);
    }

    public Mono<Customer> updateCustomer(UUID customerId, UpdateCustomerRequestDTO updateCustomerRequest) {
        return setDataOnCustomer(customerId, updateCustomerRequest)
                .zipWith(setAddressDataOnCustomer(customerId, updateCustomerRequest))
                .map(TupleUtils.function(Customer::from));
    }

    private Mono<CustomerEntity> setDataOnCustomer(UUID customerId, UpdateCustomerRequestDTO updateCustomerRequestDTO) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .map(entity -> entity.applyUpdate(updateCustomerRequestDTO))
                .flatMap(customerRepository::save);
    }

    private Mono<AddressEntity> setAddressDataOnCustomer(UUID customerId, UpdateCustomerRequestDTO updateCustomerRequestDTO) {
        return addressRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new AddressNotFoundException(customerId)))
                .map(entity -> entity.applyUpdate(updateCustomerRequestDTO))
                .flatMap(addressRepository::save);
    }
}
