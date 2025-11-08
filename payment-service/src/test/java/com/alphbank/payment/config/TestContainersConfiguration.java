package com.alphbank.payment.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @ServiceConnection
    @Bean
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:14.0")
                .withDatabaseName("payment")
                .withUsername("alph")
                .withPassword("admin");
    }

}
