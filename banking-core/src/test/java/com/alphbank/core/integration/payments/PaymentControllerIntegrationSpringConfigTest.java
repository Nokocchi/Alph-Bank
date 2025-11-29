package com.alphbank.core.integration.payments;

import com.alphbank.core.integration.IntegrationTestBase;
import com.alphbank.core.rest.model.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentControllerIntegrationSpringConfigTest extends IntegrationTestBase {

    @Test
    public void testCreatePayment() {
        CustomerDTO customer1 = webClient.post()
                .uri(uri -> uri.path("/customers").build())
                .bodyValue(createCustomerRequest(NATIONAL_ID_1))
                .exchange()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO customer2 = webClient.post()
                .uri(uri -> uri.path("/customers").build())
                .bodyValue(createCustomerRequest(NATIONAL_ID_2))
                .exchange()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CreateAccountRequestDTO createAccountRequestDTO1 = createAccountRequest(customer1.getId());
        CreateAccountRequestDTO createAccountRequestDTO2 = createAccountRequest(customer2.getId());

        AccountDTO account1 = webClient.post()
                .uri(uri -> uri.path("/accounts").build())
                .bodyValue(createAccountRequestDTO1)
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        AccountDTO account2 = webClient.post()
                .uri(uri -> uri.path("/accounts").build())
                .bodyValue(createAccountRequestDTO2)
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        CreatePaymentRequestDTO paymentRequest = createPaymentRequest(account1.getId(), account2.getIban());

        PaymentDTO payment = webClient.post()
                .uri(uri -> uri.path("/payments").build())
                .bodyValue(paymentRequest)
                .exchange()
                .expectBody(PaymentDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(payment.getFromAccountId()).isEqualTo(account1.getId());
    }


}
