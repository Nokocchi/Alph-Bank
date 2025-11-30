package com.alphbank.core.unit.service.customer;

import com.alphbank.core.customer.rest.error.model.DuplicateCustomerException;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.model.Customer;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import com.alphbank.core.unit.service.UnitTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceUnitTest extends UnitTestBase {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AddressRepository addressRepository;

    @InjectMocks
    CustomerService customerService;

    @Test
    public void testThatDuplicateKeyExceptionResultsInDuplicateCustomerException() {
        Customer customer = createCustomer(NATIONAL_ID_1);

        when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(Mono.error(new DuplicateKeyException("Duplicate!")));

        StepVerifier.create(customerService.createCustomer(customer))
                .verifyError(DuplicateCustomerException.class);

    }
}
