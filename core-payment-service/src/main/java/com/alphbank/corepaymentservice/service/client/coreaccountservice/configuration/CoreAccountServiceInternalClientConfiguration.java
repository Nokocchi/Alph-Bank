package com.alphbank.corepaymentservice.service.client.coreaccountservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CoreAccountServiceInternalClientConfiguration {

    private final CoreAccountServiceInternalClientConfigurationProperties properties;

    private final WebClient.Builder alphBaseWebClient;

    @Bean
    public WebClient coreAccountServiceClient(){
        return alphBaseWebClient
                .baseUrl(properties.getUri())
                .build();
    }

}
