package com.alphbank.signingservice.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.signingservice")
public class SigningServiceConfigurationProperties {

    private String signingServiceUrlTemplate;
}
