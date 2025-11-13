package com.alphbank.core.unit;

import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.error.DuplicateCustomerException;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.customer.service.repository.model.AddressEntity;
import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// This is a unit test: Don't use @SpringBootTest as this starts a whole ApplicationContext and web server.
// We are only testing a single class and mocking dependencies.
public class CustomerServiceTest {

    CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    AddressRepository addressRepository = Mockito.mock(AddressRepository.class);
    CustomerService customerService = new CustomerService(customerRepository, addressRepository);

    @Test
    public void testCustomerCreation() {
        UUID customerId = UUID.randomUUID();

        CreateCustomerRequest request = new CreateCustomerRequest(Locale.of("sv", "SE"), "123456789", "John", "Doe", new Address("Street", "City", "Country"));

        CustomerEntity entity = CustomerEntity.from(request);
        AddressEntity addressEntity = AddressEntity.from(customerId, request.address());

        // First time, saving goes well. Second time, throw DuplicateKeyException
        when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(Mono.just(entity))
                .thenReturn(Mono.error(new DuplicateKeyException("Duplicate!")));

        when(addressRepository.save(any(AddressEntity.class)))
                .thenReturn(Mono.just(addressEntity));

        // First call succeeds!
        StepVerifier.create(customerService.createCustomer(request))
                .expectNextMatches(c -> c.getNationalId().equals("123456789"))
                .verifyComplete();

        // Second call results in DuplicateCustomerException
        StepVerifier.create(customerService.createCustomer(request))
                .verifyError(DuplicateCustomerException.class);

    }
}
