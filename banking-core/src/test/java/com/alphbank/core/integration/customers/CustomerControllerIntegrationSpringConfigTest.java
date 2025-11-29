package com.alphbank.core.integration.customers;

import com.alphbank.core.integration.IntegrationTestBase;
import com.alphbank.core.rest.model.AddressDTO;
import com.alphbank.core.rest.model.CreateCustomerRequestDTO;
import com.alphbank.core.rest.model.CustomerDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerControllerIntegrationSpringConfigTest extends IntegrationTestBase {

    @Test
    public void testCreateCustomer() {
        CreateCustomerRequestDTO request = createCustomerRequest(NATIONAL_ID_1);
        CustomerDTO customer = webClient.post()
                .uri(uri -> uri.path("/customers").build())
                .bodyValue(request)
                .exchange()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(customer)
                .extracting(CustomerDTO::getAddress)
                .extracting(AddressDTO::getCity)
                .isEqualTo("City");

        assertThat(customer).satisfies(req -> {
            assertThat(req.getFirstName()).isEqualTo("John");
            assertThat(req.getLanguage()).isEqualTo("sv");
            assertThat(req.getAddress().getCountry()).isEqualTo("Country");
        });
    }
}
