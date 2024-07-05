package com.alphbank.loanapplicationservice.service.client.coreloanservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.client.core-loan-service")
public class CoreLoanServiceClientConfigurationProperties {

    private String uri;


}
