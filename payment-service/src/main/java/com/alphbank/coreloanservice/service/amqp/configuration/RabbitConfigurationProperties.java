package com.alphbank.coreloanservice.service.amqp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitConfigurationProperties {

    private String host;
    private String username;
    private String password;
    private String paymentSigningStatusRoutingKey;
    private String paymentSigningStatusQueue;
    private String paymentSigningStatusExchange;
}
