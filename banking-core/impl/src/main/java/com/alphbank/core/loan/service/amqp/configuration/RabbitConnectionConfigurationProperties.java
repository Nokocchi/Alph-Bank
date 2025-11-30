package com.alphbank.core.loan.service.amqp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitConnectionConfigurationProperties {

    private String host;
    private String username;
    private String password;
}
