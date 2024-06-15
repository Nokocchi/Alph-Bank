package com.alphbank.reactivelogging.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reactivelogging")
public class ReactiveLoggingProperties {

    private boolean enabled;
}
