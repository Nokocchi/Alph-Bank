package com.alphbank.payment.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@Data
@Component
@ConfigurationProperties(prefix = "alph.client.payment-service")
public class PaymentServiceConfigurationProperties {

    private String basketStatusURITemplate;
    private String basketAuthorizationStatusURITemplate;
    private String basketAuthorizationURITemplate;


}
