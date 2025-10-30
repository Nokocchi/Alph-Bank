package com.alphbank.commons.autoconfigure;

import com.alphbank.commons.impl.JsonLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.jackson.datatype.money.MoneyModule;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.money.MonetaryAmount;

@AutoConfiguration
@EnableConfigurationProperties(CommonsProperties.class)
public class CommonsConfiguration {

    private final CommonsProperties properties;

    public CommonsConfiguration(CommonsProperties properties) {
        this.properties = properties;
    }

    // Overwrites the representation of MonetaryAmount in Swagger so that it looks like a simple amount-currency tuple
    static {
        SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, org.springdoc.core.converters.models.MonetaryAmount.class);
    }

    // Custom serializer/deserializer for the org.javamoney.moneta implementation of javax.MonetaryAmount as a simple (Amount, Currency) tuple
    // Declaring the Jackson module as a bean is enough to automatically add it to the ObjectMapper
    @Bean
    public MoneyModule moneyModule() {
        return new MoneyModule();
    }

    @Bean(name = "alphBaseWebClientBuilder")
    WebClient.Builder alphBaseWebClientBuilder(ObjectMapper objectMapper) {
        WebClient.Builder webClientBuilder = WebClient.builder()
                .codecs(configurer -> overrideDefaultCodecs(configurer.defaultCodecs(), objectMapper))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (properties.isWiretapEnabled()) {
            HttpClient httpClient = HttpClient
                    .create()
                    .wiretap("reactor.netty.http.client.HttpClient",
                            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }

        return webClientBuilder;
    }

    // Spring Webclient uses its own ObjectMapper, which means that the one I have configured is not used.
    // Which means that MonetaryAmount and date/time encoding is not done correctly in WebClient
    // As a fix, I set the encoder and decoder to use my configured ObjectMapper
    private void overrideDefaultCodecs(ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs, ObjectMapper objectMapper) {
        defaultCodecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        defaultCodecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        //defaultCodecs.serverSentEventDecoder(new Jackson2JsonDecoder(objectMapper));
    }

    @ConditionalOnProperty(name = "alph-commons.wiretapEnabled", havingValue = "true")
    @Component("ServerRequestWiretap")
    public class MyNettyWebServerCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
        @Override
        public void customize(NettyReactiveWebServerFactory factory) {
            factory.addServerCustomizers(httpServer -> httpServer.wiretap("reactor.netty.http.server.HttpServer",
                    LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL));
        }
    }

    @Bean
    JsonLog jsonLog(ObjectMapper objectMapper){
        return new JsonLog(objectMapper);
    }
}
