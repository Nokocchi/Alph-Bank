package com.alphbank.corepaymentservice.service.client.coreaccountservice;

import com.alphbank.corepaymentservice.service.client.coreaccountservice.model.AccountBalanceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CoreAccountServiceInternalClient {

    private final WebClient coreAccountServiceClient;

    public Mono<String> updateBalances(AccountBalanceUpdateRequest request){
        System.out.println("About to transfer between accounts");
        return coreAccountServiceClient
                .post()
                .uri("/account_transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                //.onStatus(HttpStatusCode::is1xxInformational, clientResponse -> Mono.error(new RuntimeException()))
                //.onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new RuntimeException()))
                //.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(test(clientResponse)))
                //.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException()))
                .bodyToMono(String.class)
                .doOnError(RuntimeException.class, System.out::println)
                .doOnError(WebClientResponseException.class, e -> System.out.println("TESTIT " + e.getResponseBodyAsString()))
                .doOnNext(System.out::println);
    }

    private Throwable test(ClientResponse clientResponse) {
        return new RuntimeException();
    }

}
