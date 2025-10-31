package com.alphbank.core.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class SpringBootStarterTestConfiguration {

    @Bean
    public WebClient alphWebClient(WebClient.Builder alphBaseWebClientBuilder) {
        return alphBaseWebClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }

}
