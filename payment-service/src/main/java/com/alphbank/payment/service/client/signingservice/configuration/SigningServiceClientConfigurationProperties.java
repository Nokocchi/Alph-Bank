package com.alphbank.payment.service.client.signingservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "alph.client.signing-service")
public class SigningServiceClientConfigurationProperties {

    private String uri;
    private String singlePaymentDocumentToSignTemplate;
    private String periodicPaymentDocumentToSignTemplate;

    @PostConstruct
    void postConstruction(){
        log.info("{}", this);
    }
}
