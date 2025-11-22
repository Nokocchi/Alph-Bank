package com.alphbank.payment.service.client.signingservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SigningServiceClientConfiguration {

    private final SigningServiceClientConfigurationProperties properties;

    @Bean
    public WebClient signingServiceWebClient2(WebClient.Builder alphWebClientBuilder, ObjectMapper objectMapper) {
        alphWebClientBuilder.defaultHeaders(headers -> {
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        });
        return alphWebClientBuilder.baseUrl(properties.getUri()).build();
    }
}
