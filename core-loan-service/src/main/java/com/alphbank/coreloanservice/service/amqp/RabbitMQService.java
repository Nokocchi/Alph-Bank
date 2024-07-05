package com.alphbank.coreloanservice.service.amqp;

import com.alphbank.coreloanservice.service.client.coreaccountservice.model.PayoutLoanRequest;
import com.alphbank.coreloanservice.service.amqp.configuration.RabbitConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfigurationProperties properties;

    public Mono<Void> send(PayoutLoanRequest accountBalanceUpdateRequest) {
        return Mono.fromRunnable(() -> {
            log.info("Sending message about loan {}, with debtorAccountId {} and amount {} to RabbitMQ exchange '{}' with routing key '{}'",
                    accountBalanceUpdateRequest.loanReference(),
                    accountBalanceUpdateRequest.debtorAccountId(),
                    accountBalanceUpdateRequest.principal(),
                    properties.getExchange(),
                    properties.getRoutingKey());
            rabbitTemplate.convertAndSend(properties.getRoutingKey(), accountBalanceUpdateRequest);
            log.info("Message about loan {} successfully sent!", accountBalanceUpdateRequest.loanReference());
        });
    }
}
