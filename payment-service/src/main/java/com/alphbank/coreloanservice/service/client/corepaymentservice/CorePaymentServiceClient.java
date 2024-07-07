package com.alphbank.coreloanservice.service.client.corepaymentservice;

import com.alphbank.coreloanservice.service.client.corepaymentservice.model.CreateCorePaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CorePaymentServiceClient {

    private final WebClient corePaymentServiceWebClient;

    public Mono<Void> sendPaymentToCore(CreateCorePaymentRequest request){
        return corePaymentServiceWebClient
                .post()
                .uri("/loan")
                .bodyValue(request)
                .exchangeToMono(ClientResponse::releaseBody);
    }

}
