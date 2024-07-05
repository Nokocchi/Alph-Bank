package com.alphbank.loanapplicationservice.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alph.loanapplicationservice")
public class LoanApplicationConfigurationProperties {


}
