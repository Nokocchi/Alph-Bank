package com.alphbank.core.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableWireMock
public class WiremockExampleTest extends IntegrationTestBase {

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    // BankingCore does not make any external calls, so wiremock is not needed here.
    // I'm leaving this here until I add integration tests to upstream services.

    @Test
    public void testIt() {
        stubFor(get("/ping").willReturn(ok("pong")));

        RestClient client = RestClient.create();
        String body = client.get()
                .uri(wireMockUrl + "/ping")
                .retrieve()
                .body(String.class);

        assertThat(body).isEqualTo("pong");
    }
}
