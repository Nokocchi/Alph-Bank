package com.alphbank.commons.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonsConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonsConfiguration.class))
            .withPropertyValues("alph-commons.wiretapEnabled=true")
            .withBean("objectMapper", ObjectMapper.class, new ObjectMapper());

    @Test
    void testContextRunner() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("ServerRequestWiretap"));
            assertTrue(context.containsBean("jsonLog"));
        });
    }
}
