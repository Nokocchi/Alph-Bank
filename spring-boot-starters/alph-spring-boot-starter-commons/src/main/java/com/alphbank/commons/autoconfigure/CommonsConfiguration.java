package com.alphbank.commons.autoconfigure;

import com.alphbank.commons.impl.JsonLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.jackson.datatype.money.MoneyModule;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@AutoConfiguration
@EnableConfigurationProperties(CommonsProperties.class)
public class CommonsConfiguration {

    private final CommonsProperties properties;

    public CommonsConfiguration(CommonsProperties properties) {
        this.properties = properties;
    }

    // Custom serializer/deserializer for the org.javamoney.moneta implementation of javax.MonetaryAmount as a simple (Amount, Currency) tuple
    // Declaring the Jackson module as a bean is enough to automatically add it to the ObjectMapper
    // We don't need this in the DTO/rest/webclient layer due to the generated classes having their own implementation
    // But we might want to log MonetaryAmounts with jsonLog in the business logic somewhere
    @Bean
    public MoneyModule moneyModule() {
        return new MoneyModule();
    }

    @Bean(name = "alphBaseWebClientBuilder")
    WebClient.Builder alphBaseWebClientBuilder() {
        WebClient.Builder webClientBuilder = WebClient.builder()
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

    @Bean(name = "alphWebClient")
    WebClient alphWebClient(WebClient.Builder alphBaseWebClientBuilder) {
        return alphBaseWebClientBuilder.build();
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
    JsonLog jsonLog(ObjectMapper objectMapper) {
        return new JsonLog(objectMapper);
    }
}
