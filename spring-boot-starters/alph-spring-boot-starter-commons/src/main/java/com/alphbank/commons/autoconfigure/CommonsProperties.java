package com.alphbank.commons.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alph-commons")
public class CommonsProperties {

    private boolean wiretapEnabled;

}
