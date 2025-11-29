package com.alphbank.core.integration.accounts;

import com.alphbank.core.integration.IntegrationTestBase;
import com.alphbank.core.rest.model.*;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountControllerIntegrationSpringConfigTest extends IntegrationTestBase {

    @Test
    public void testCreateAccount() {
        CustomerDTO customer = webClient.post()
                .uri(uri -> uri.path("/customers").build())
                .bodyValue(createCustomerRequest(NATIONAL_ID_1))
                .exchange()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CreateAccountRequestDTO createAccountRequestDTO = createAccountRequest(customer.getId());

        AccountDTO account = webClient.post()
                .uri(uri -> uri.path("/accounts").build())
                .bodyValue(createAccountRequestDTO)
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(account.getAccountName()).isEqualTo(createAccountRequestDTO.getAccountName());
    }



}
