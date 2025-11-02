package com.alphbank.core.integration;


import com.alphbank.core.integration.config.TestContainersConfiguration;
import org.springframework.context.annotation.Import;

@Import({TestContainersConfiguration.class})
public class IntegrationTestBase {

}
