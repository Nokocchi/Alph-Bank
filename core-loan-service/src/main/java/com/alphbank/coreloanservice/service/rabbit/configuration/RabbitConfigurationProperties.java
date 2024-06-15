package com.alphbank.coreloanservice.service.rabbit.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitConfigurationProperties {

    @Value("${host}")
    private String host;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Value("${routing-key}")
    private String routingKey;

    @Value("${exchange}")
    private String exchange;
}
