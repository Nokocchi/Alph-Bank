package com.alphbank.loanapplicationservice.service.client.signingservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class SigningServiceClientConfiguration {

    private final SigningServiceClientConfigurationProperties properties;

    @Bean
    public WebClient signingServiceWebClient(WebClient.Builder alphBaseWebClientBuilder){
        return alphBaseWebClientBuilder
                .baseUrl(properties.getUri())
                .build();
    }
}
