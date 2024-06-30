package com.alphbank.corepaymentservice.service.client.coreaccountservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CoreAccountServiceInternalClientConfiguration {

    private final CoreAccountServiceInternalClientConfigurationProperties properties;

    @Bean
    public WebClient coreAccountServiceClient(WebClient.Builder alphBaseWebClientBuilder){
        return alphBaseWebClientBuilder
                .baseUrl(properties.getUri())
                .build();
    }

}
