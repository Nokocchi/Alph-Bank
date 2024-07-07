package com.alphbank.coreloanservice.service.client.corepaymentservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CorePaymentServiceClientConfiguration {

    private final CorePaymentServiceClientConfigurationProperties properties;

    @Bean
    public WebClient corePaymentServiceWebClient(WebClient.Builder alphBaseWebClientBuilder){
        return alphBaseWebClientBuilder
                .baseUrl(properties.getUri())
                .build();
    }
}
