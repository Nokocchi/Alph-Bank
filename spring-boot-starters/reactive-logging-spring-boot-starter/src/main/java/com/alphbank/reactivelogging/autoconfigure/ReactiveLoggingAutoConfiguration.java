package com.alphbank.reactivelogging.autoconfigure;

import com.alphbank.reactivelogging.impl.StarterImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.jackson.datatype.money.MoneyModule;

import javax.money.MonetaryAmount;

@AutoConfiguration
@EnableConfigurationProperties(ReactiveLoggingProperties.class)
public class ReactiveLoggingAutoConfiguration {

    private final ReactiveLoggingProperties properties;

    public ReactiveLoggingAutoConfiguration(ReactiveLoggingProperties properties) {
        this.properties = properties;
    }

    // TODO: Test if this still works now that it's in a starter
    // Overwrites the representation of MonetaryAmount in Swagger so that it looks like a simple amount-currency tuple
    static {
        SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, org.springdoc.core.converters.models.MonetaryAmount.class);
    }

    // Custom serializer/deserializer for the org.javamoney.moneta implementation of javax.MonetaryAmount as a simple amount-currency tuple
    // Declaring the Jackson module as a bean is enough to automatically add it to the ObjectMapper
    @Bean
    public MoneyModule moneyModule() {
        return new MoneyModule();
    }

    @Bean
    public StarterImpl starterImpl() {
        return new StarterImpl(properties.enabled());
    }

    @Bean(name = "alphBaseWebClient")
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
