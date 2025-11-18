package com.alphbank.payment.service.client.signingservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.client.signing-service")
public class SigningServiceClientConfigurationProperties {

    private String uri;
    private String singlePaymentDocumentToSignTemplate;
    private String getPeriodicPaymentDocumentToSignTemplate;
}
