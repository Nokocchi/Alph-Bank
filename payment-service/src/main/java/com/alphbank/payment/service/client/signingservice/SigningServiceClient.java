package com.alphbank.payment.service.client.signingservice;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionRequest;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class SigningServiceClient {

    private final WebClient signingServiceWebClient;
    private final JsonLog jsonLog;

    public Mono<SetupSigningSessionResponse> setupSigningSession(SetupSigningSessionRequest request){
        log.info("Sending SetupSigningSessionRequest to signing-service {}", jsonLog.format(request));
        return signingServiceWebClient
                .post()
                .uri("/signing")
                .bodyValue(request)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(SetupSigningSessionResponse.class));
    }

}
