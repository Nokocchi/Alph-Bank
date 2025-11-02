package com.alph.core.customer;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.customer.rest.CustomerController;
import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.Customer;
import com.alphbank.core.customer.service.CustomerService;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = JsonLog.class)
public class CustomerBase {

    CustomerService customerService = Mockito.mock(CustomerService.class);
    JsonLog jsonLog = Mockito.mock(JsonLog.class);

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.standaloneSetup(new CustomerController(customerService, jsonLog));

        Customer customer = new Customer(UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"), new Address("street", "city", "country"), "phone", "nationalId", "firstName", "lastName", Locale.of("sv", "SE"));
        when(customerService.findAllCustomers()).thenReturn(Flux.just(customer));
    }
}
