package com.alphbank.corepaymentservice.service.client.coreaccountservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BaseWebClientConfiguration {

    @Bean
    WebClient.Builder alphBaseWebClient(ObjectMapper objectMapper){
        return WebClient.builder()
                .codecs(configurer -> overrideDefaultCodecs(configurer.defaultCodecs(), objectMapper))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }

    // Spring Webclient uses its own ObjectMapper, which means that the one I have configured is not used.
    // Which means that MonetaryAmount and date/time encoding is not done correctly in WebClient
    // As a fix, I set the encoder and decoder to use my configured ObjectMapper
    private void overrideDefaultCodecs(ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs, ObjectMapper objectMapper) {
        defaultCodecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        defaultCodecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        //defaultCodecs.serverSentEventDecoder(new Jackson2JsonDecoder(objectMapper));
    }

}
