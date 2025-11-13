package com.alphbank.payment.service.client.corepaymentservice.configuration;

import com.alphbank.core.ApiClient;
import com.alphbank.core.client.CorePaymentClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CorePaymentServiceClientConfiguration {

    private final CorePaymentServiceClientConfigurationProperties properties;

    // ApiClient uses its own basePath and headers.
    // But we still need our webclient for other configurations, like filters, logging, custom codecs, etc., so lets give it our Webclient.
    // Also providing our default object mapper which we have configured in the commons starter. Otherwise it uses its own
    @Bean
    public CorePaymentClient corePaymentApi(WebClient alphWebClient, ObjectMapper objectMapper) {
        ApiClient apiClient = new ApiClient(alphWebClient, objectMapper, ApiClient.createDefaultDateFormat())
                .setBasePath(properties.getUri())
                .addDefaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addDefaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new CorePaymentClient(apiClient);
    }
}
