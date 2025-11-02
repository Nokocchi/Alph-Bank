package com.alphbank.core.integration.customer;

import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
import com.alphbank.core.customer.rest.model.Customer;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.integration.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// Random Port auto-configures a WebTestClient that we can use. The random port ensures that we won't run into port bind conflicts when running multiple test classes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CustomerControllerIntegrationSpringConfigTest extends IntegrationTestBase {

    @Autowired
    CustomerRepository repository;

    @BeforeEach
    public void cleanup() {
        repository.deleteAll().block();
    }

    @Autowired
    WebTestClient webClient;

    @Test
    public void testCreateCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest(Locale.of("sv", "SE"), "123456789", "John", "Doe", new Address("Street", "City", "Country"));
        Customer customer = webClient.post()
                .uri(uri -> uri.path("/customer").build())
                .bodyValue(request)
                .exchange()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(customer)
                .extracting(Customer::getAddress)
                .extracting(Address::city)
                .isEqualTo("City");

        assertThat(customer).satisfies(req -> {
            assertThat(req.getFirstName()).isEqualTo("John");
            assertThat(req.getLocale().getLanguage()).isEqualTo("sv");
            assertThat(req.getAddress().country()).isEqualTo("Country");
        });
    }
}
