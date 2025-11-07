package com.alphbank.payment.service.client.corepaymentservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.client.core-payment-service")
public class CorePaymentServiceClientConfigurationProperties {

    private String uri;


}
