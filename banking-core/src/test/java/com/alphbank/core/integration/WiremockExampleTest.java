package com.alphbank.core.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
public class WiremockExampleTest extends IntegrationTestBase {

    @Autowired
    WebTestClient webClient;

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    // BankingCore does not make any external calls, so wiremock is not needed here.
    // I'm leaving this here until I add integration tests to upstream services.

    @Test
    public void testIt() {
        stubFor(get("/ping").willReturn(ok("pong")));

        String body = webClient.get()
                .uri(wireMockUrl + "/ping")
                .exchange()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).isEqualTo("pong");
    }
}
