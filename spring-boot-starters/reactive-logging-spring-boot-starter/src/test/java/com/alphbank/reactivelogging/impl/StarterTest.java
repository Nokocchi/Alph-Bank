package com.alphbank.reactivelogging.impl;

import com.alphbank.reactivelogging.autoconfigure.ReactiveLoggingAutoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StarterTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ReactiveLoggingAutoConfiguration.class))
            .withBean("objectMapper", ObjectMapper.class, new ObjectMapper());

    @Test
    void testContextRunner() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("starterImpl"));
        });
    }
}
