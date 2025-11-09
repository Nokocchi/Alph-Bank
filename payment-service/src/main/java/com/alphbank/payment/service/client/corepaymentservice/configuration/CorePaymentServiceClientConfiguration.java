package com.alphbank.payment.service.client.corepaymentservice.configuration;

import com.alphbank.core.ApiClient;
import com.alphbank.core.client.CorePaymentClient;
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
    // But we still need the webclient for other configurations, like filters, logging, custom codecs, etc.
    @Bean
    public CorePaymentClient corePaymentApi(WebClient alphWebClient) {
        ApiClient apiClient = new ApiClient(alphWebClient)
                .setBasePath(properties.getUri())
                .addDefaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addDefaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new CorePaymentClient(apiClient);
    }
}
