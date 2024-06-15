package com.alphbank.coreloanservice.service.client.coreaccountservice.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "client.coreaccountservice.internal")
public class CoreAccountServiceInternalClientConfigurationProperties {

    @Value("${uri}")
    private String uri;


}
