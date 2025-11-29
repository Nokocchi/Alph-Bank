package com.alphbank.core.customer;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.customer.rest.CustomerController;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.model.Address;
import com.alphbank.core.customer.service.model.Customer;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class CustomerBase {

    CustomerService customerService = Mockito.mock(CustomerService.class);
    JsonLog jsonLog = Mockito.mock(JsonLog.class);

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.standaloneSetup(new CustomerController(customerService, jsonLog));

        when(customerService.findAllCustomers()).thenReturn(Flux.just(createCustomer()));
    }

    protected Customer createCustomer() {
        return Customer.builder()
                .id(UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"))
                .nationalId("nationalId")
                .locale(Locale.of("sv", "SE"))
                .firstName("firstName")
                .lastName("lastName")
                .address(Address.builder()
                        .streetAddress("street")
                        .city("city")
                        .country("country")
                        .build())
                .build();
    }
}
