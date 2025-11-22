package com.alphbank.payment.client;

import com.alphbank.payment.integration.IntegrationTestBase;
import com.alphbank.payment.rest.model.request.CreatePaymentRequest;
import com.alphbank.payment.rest.model.response.InternalPaymentDTO;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(
        ids = "com.alphbank.core:BankingCore:+:stubs:8081",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class CorePaymentStubsTest extends IntegrationTestBase {

    WebTestClient webClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8081")
            .build();

    @Test
    public void testCreatePaymentHappyPath() {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest("string", "string", "string", "string", Money.of(22, "SEK"), LocalDateTime.parse("2015-08-04T10:11:30"));

        // If this doesn't work after fixing the stub in BankingCore, then try to reintroduce the Autowired WebTestclient and @SpringBootTest..
        InternalPaymentDTO response = webClient.post()
                .uri( "/payment")
                // Do we need something like this? .header("accepts", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(paymentRequest)
                .exchange()
                .expectBody(InternalPaymentDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
    }
}