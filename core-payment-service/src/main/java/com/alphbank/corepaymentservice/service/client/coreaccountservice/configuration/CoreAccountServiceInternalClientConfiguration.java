package com.alphbank.corepaymentservice.service.client.coreaccountservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CoreAccountServiceInternalClientConfiguration {

    private final CoreAccountServiceInternalClientConfigurationProperties properties;

    @Bean
    public WebClient coreAccountServiceClient(){
        return WebClient.builder()
                .baseUrl(properties.getUri())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
