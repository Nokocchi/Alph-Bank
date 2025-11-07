package com.alphbank.payment.integration;

import com.alphbank.payment.rest.model.CreatePaymentRequest;
import com.alphbank.payment.rest.model.Payment;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "com.alphbank.core:BankingCore:+:stubs:8081",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class WiremockExampleTest extends IntegrationTestBase {

    @Autowired
    WebTestClient webClient;

    @Test
    public void testIt() {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"), UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"), "string", "string", "string", Money.of(22, "SEK"), LocalDateTime.parse("2015-08-04T10:11:30"));


        Payment response = webClient.post()
                .uri( "http://localhost:8081/payment")
                .bodyValue(paymentRequest)
                .exchange()
                .expectBody(Payment.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
    }
}