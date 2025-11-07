package com.alphbank.core.unit;

import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.customer.service.repository.model.AddressEntity;
import com.alphbank.core.integration.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@Import(TestContainersConfiguration.class)
@SpringBootTest
public class CustomerServiceTransactionTest {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @MockitoBean
    AddressRepository addressRepository;

    @Test
    public void testCustomerCreationRollbackOnAddressSaveError() {
        CreateCustomerRequest request = new CreateCustomerRequest(Locale.of("sv", "SE"), "123456789", "John", "Doe", new Address("Street", "City", "Country"));

        // Throw error when saving the address, which happens after saving the customer.
        when(addressRepository.save(any(AddressEntity.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Address save failed!")));

        StepVerifier.create(customerService.createCustomer(request))
                .verifyError(DataIntegrityViolationException.class);

        // Checking the repository afterwards. There should be no customer there, as it was rolled back due to the error from AddressRepository.
        StepVerifier.create(customerRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }
}
