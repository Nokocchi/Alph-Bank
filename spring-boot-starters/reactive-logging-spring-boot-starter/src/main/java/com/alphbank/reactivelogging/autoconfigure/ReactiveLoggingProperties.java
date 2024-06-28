package com.alphbank.reactivelogging.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reactivelogging")
public record ReactiveLoggingProperties(boolean enabled) {
}
