package com.alphbank.loanapplicationservice.service.client.coreloanservice;

import com.alphbank.loanapplicationservice.service.client.coreloanservice.model.CreateLoanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CoreLoanServiceClient {

    private final WebClient coreLoanServiceWebClient;

    public Mono<Void> createLoan(CreateLoanRequest request){
        return coreLoanServiceWebClient
                .post()
                .uri("/loan")
                .bodyValue(request)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Void.class));
    }

}
