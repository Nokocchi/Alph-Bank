package com.alphbank.core.integration;

import com.alphbank.core.integration.config.SpringBootStarterTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest
@Import(SpringBootStarterTestConfiguration.class)
@EnableWireMock
public class WiremockExampleTest {

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @Autowired
    private WebClient alphWebClient;

    // BankingCore does not make any external calls, so wiremock is not needed here.
    // I'm leaving this here until I add integration tests to upstream services.

    @Test
    public void testIt() {
        assert true;
    }

}
