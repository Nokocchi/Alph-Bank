package com.alphbank.core.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    // This test container is defined as a bean, so it will be re-used across the full test suite
    // It can be created by class by defining it as a static field, but then I get "address already in use"-exception.
    // It's also possible to mark each test class with @DirtiesContext to force Spring to re-create this bean
    // Another alternative is to use Junit's annotations like @Container and @TestContainers and handle the lifecycle myself
    // But then I also need to write a big ugly DynamicPropertyValues() method each time, with r2dbc AND flyway config values..
    @ServiceConnection
    @Bean
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:14.0")
                .withDatabaseName("core")
                .withUsername("alph")
                .withPassword("admin");
    }

}
