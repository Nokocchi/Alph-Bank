package com.alphbank.loanapplicationservice.service.amqp;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.loanapplicationservice.service.LoanApplicationService;
import com.alphbank.loanapplicationservice.service.amqp.configuration.RabbitConfigurationProperties;
import com.alphbank.loanapplicationservice.service.amqp.model.SigningSession;
import com.alphbank.loanapplicationservice.service.amqp.model.SigningStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQListener {

    private final JsonLog jsonLog;
    private final LoanApplicationService loanApplicationService;

    @RabbitListener(queues = "${spring.rabbitmq.loan-application-signing-status-queue}")
    public void handleMessage(SigningSession signingSession) {
        log.info("Received signing session on RabbitMQ! {}", jsonLog.format(signingSession));
        loanApplicationService.updateLoanApplicationStatus(signingSession.getSigningSessionId(), signingSession.getSigningStatus())
                .subscribe();
    }
}
