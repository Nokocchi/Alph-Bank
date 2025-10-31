package com.alphbank.core.integration.customer;

import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.CreateCustomerRequest;
import com.alphbank.core.customer.rest.model.Customer;
import com.alphbank.core.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// Defaults to MOCK, but we need the server to start up on the port we defined (8080)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CustomerControllerIntegrationTest extends IntegrationTestBase {

    @Test
    public void testCreateCustomer(){
        CreateCustomerRequest request = new CreateCustomerRequest(Locale.of("sv", "SE"), "123456789", "John", "Doe", new Address("Street", "City", "Country"));
        Customer customer = alphWebClient.post()
                .uri(uri -> uri.path("/customer").build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Customer.class)
                .block();

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
