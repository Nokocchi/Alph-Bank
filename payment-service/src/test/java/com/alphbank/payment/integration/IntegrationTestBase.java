package com.alphbank.payment.integration;


import com.alphbank.payment.config.TestContainersConfiguration;
import org.springframework.context.annotation.Import;

@Import({TestContainersConfiguration.class})
public class IntegrationTestBase {

}
