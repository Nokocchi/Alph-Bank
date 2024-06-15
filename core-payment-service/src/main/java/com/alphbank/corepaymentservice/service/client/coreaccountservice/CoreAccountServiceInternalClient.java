package com.alphbank.corepaymentservice.service.client.coreaccountservice;

import com.alphbank.corepaymentservice.service.client.coreaccountservice.model.AccountBalanceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class CoreAccountServiceInternalClient {

    private final WebClient coreAccountServiceClient;

    public Mono<String> updateBalances(AccountBalanceUpdateRequest request){
        return coreAccountServiceClient
                .post()
                .uri("/account_transfer")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .exchangeToMono(clientResponse -> {
                    System.out.println(clientResponse);
                    return clientResponse.bodyToMono(String.class);
                })
                .doOnError(RuntimeException.class, System.out::println);
    }

}
