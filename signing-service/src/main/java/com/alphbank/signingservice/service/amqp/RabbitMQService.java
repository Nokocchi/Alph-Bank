package com.alphbank.signingservice.service.amqp;

import com.alphbank.signingservice.rest.model.SigningSession;
import com.alphbank.signingservice.service.amqp.configuration.RabbitConfigurationProperties;
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

    public Mono<Void> send(SigningSession signingSession) {
        return Mono.fromRunnable(() -> {
            String exchange = properties.getExchange();
            String routingKey = signingSession.getSigningStatusUpdatedRoutingKey();
            log.info("Sending signing session {} on RabbitMQ on exchange {} and with routing key {}", signingSession.getSigningSessionId(), exchange, routingKey);
            rabbitTemplate.convertAndSend(exchange, routingKey, signingSession);
            log.info("Message about signing session {} successfully sent!", signingSession.getSigningSessionId());
        });
    }
}
