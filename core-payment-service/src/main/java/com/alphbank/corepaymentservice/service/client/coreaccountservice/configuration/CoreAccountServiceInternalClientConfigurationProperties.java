package com.alphbank.corepaymentservice.service.client.coreaccountservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.client.coreaccountservice.internal")
public class CoreAccountServiceInternalClientConfigurationProperties {

    private String uri;


}
