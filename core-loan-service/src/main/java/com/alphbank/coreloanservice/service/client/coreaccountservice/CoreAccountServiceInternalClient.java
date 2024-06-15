package com.alphbank.coreloanservice.service.client.coreaccountservice;

import com.alphbank.coreloanservice.service.client.coreaccountservice.model.PayoutLoanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CoreAccountServiceInternalClient {

    private final WebClient coreAccountServiceClient;

    public Mono<Void> payoutLoan(PayoutLoanRequest request){
        return coreAccountServiceClient
                .post()
                .uri("/loan_payout")
                .bodyValue(request)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Void.class));
    }

}
