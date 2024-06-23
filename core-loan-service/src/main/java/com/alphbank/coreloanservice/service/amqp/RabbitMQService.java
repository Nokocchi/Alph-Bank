package com.alphbank.coreloanservice.service.amqp;

import com.alphbank.coreloanservice.service.client.coreaccountservice.model.PayoutLoanRequest;
import com.alphbank.coreloanservice.service.amqp.configuration.RabbitConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfigurationProperties properties;

    public Mono<Void> send(PayoutLoanRequest accountBalanceUpdateRequest) {
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(properties.getRoutingKey(), accountBalanceUpdateRequest));
    }
}
