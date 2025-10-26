package com.alphbank.core.loan.service.amqp.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final RabbitConnectionConfigurationProperties rabbitConnectionConfigurationProperties;
    private final RabbitMessageConfigurationProperties rabbitMessageConfigurationProperties;

    @Bean
    public TaskScheduler taskScheduler() {
        return new SimpleAsyncTaskScheduler();
    }

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitConnectionConfigurationProperties.getHost());
        connectionFactory.setUsername(rabbitConnectionConfigurationProperties.getUsername());
        connectionFactory.setPassword(rabbitConnectionConfigurationProperties.getPassword());
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin amqpAdmin(CachingConnectionFactory rabbitConnectionFactory) {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory rabbitConnectionFactory, ObjectMapper objectMapper) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(rabbitMessageConfigurationProperties.getExchange());
    }
}