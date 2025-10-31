package com.alphbank.core.integration;


import com.alphbank.core.integration.config.SpringBootStarterTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(SpringBootStarterTestConfiguration.class)
public class IntegrationTestBase {

    @Autowired
    public WebClient alphWebClient;

}
