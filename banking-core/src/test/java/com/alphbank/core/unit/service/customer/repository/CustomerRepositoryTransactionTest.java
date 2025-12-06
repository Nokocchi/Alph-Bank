package com.alphbank.core.unit.service.customer.repository;

import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.customer.service.repository.model.AddressEntity;
import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import com.alphbank.core.integration.config.TestContainersConfiguration;
import com.alphbank.core.unit.service.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import({CustomerService.class, TestContainersConfiguration.class})
@DataR2dbcTest
public class CustomerRepositoryTransactionTest extends UnitTestBase {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @MockitoBean
    AddressRepository addressRepository;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll().block();
    }

    @Test
    void testCustomerCreationRollbackOnAddressSaveError() {
        Customer customer = createCustomer(NATIONAL_ID_1);

        // Throw error when saving the address, which happens after saving the customer.
        when(addressRepository.save(any(AddressEntity.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Address save failed!")));

        StepVerifier.create(customerService.createCustomer(customer))
                .verifyError(DataIntegrityViolationException.class);

        // Checking the repository afterwards. There should be no customer there, as it was rolled back due to the error from AddressRepository.
        StepVerifier.create(customerRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testUniqueConstraintOnCustomerNationalIdAndCountryCode() {
        Customer identicalCustomer1 = createCustomer(UUID.randomUUID(), "SE", NATIONAL_ID_1);
        Customer identicalCustomer2 = createCustomer(UUID.randomUUID(), "SE", NATIONAL_ID_1);
        Customer customerWithDifferentCountry = createCustomer(UUID.randomUUID(), "NO", NATIONAL_ID_1);
        Customer customerWithDifferentNationalId = createCustomer(UUID.randomUUID(), "SE", NATIONAL_ID_2);

        StepVerifier.create(customerRepository.save(CustomerEntity.from(identicalCustomer1)))
                .expectNextCount(1)
                .verifyComplete();

        // The exact duplicate results in an error
        StepVerifier.create(customerRepository.save(CustomerEntity.from(identicalCustomer2)))
                .verifyError(DuplicateKeyException.class);

        // But the customers with different country code or national id are accepted
        StepVerifier.create(customerRepository.save(CustomerEntity.from(customerWithDifferentCountry)))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(customerRepository.save(CustomerEntity.from(customerWithDifferentNationalId)))
                .expectNextCount(1)
                .verifyComplete();
    }
}
