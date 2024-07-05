package com.alphbank.loanapplicationservice.service.client.coreloanservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CoreLoanServiceClientConfiguration {

    private final CoreLoanServiceClientConfigurationProperties properties;

    @Bean
    public WebClient coreLoanServiceWebClient(WebClient.Builder alphBaseWebClientBuilder){
        return alphBaseWebClientBuilder
                .baseUrl(properties.getUri())
                .build();
    }
}
