package com.alphbank.payment.service.amqp;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.service.PaymentService;
import com.alphbank.payment.service.amqp.model.SigningSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQListener {

    private final JsonLog jsonLog;
    private final PaymentService paymentService;

    @RabbitListener(queues = "${spring.rabbitmq.payment-signing-status-queue}")
    public void handleMessage(SigningSession signingSession) {
        log.info("Received signing session on RabbitMQ! {}", jsonLog.format(signingSession));
        paymentService.handleNewSigningStatus(signingSession.getSigningSessionId(), signingSession.getSigningStatus())
                .subscribe();
    }
}
