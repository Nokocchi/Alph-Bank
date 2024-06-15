package com.alphbank.coreloanservice.service.client.coreaccountservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CoreAccountServiceInternalClientConfiguration {

    private final CoreAccountServiceInternalClientConfigurationProperties properties;

    @Bean
    public WebClient coreAccountServiceClient(){
        return WebClient.builder()
                .baseUrl(properties.getUri())
                .build();
    }
}
