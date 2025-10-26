package com.alphbank.core.loan.service.amqp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.loan.rabbitmq")
public class RabbitMessageConfigurationProperties {

    private String routingKey;
    private String exchange;
}
