package com.alphbank.coreaccountservice.rest;

import com.alphbank.coreaccountservice.rest.model.Account;
import com.alphbank.coreaccountservice.rest.model.AccountSearchResponse;
import com.alphbank.coreaccountservice.rest.model.CreateAccountRequest;
import com.alphbank.coreaccountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<AccountSearchResponse>> searchAccounts(@RequestParam(name = "customer_id") String customerId){
        System.out.println(String.format("Received search response with customerId %s!", customerId));
        return accountService.getAllAccountsByCustomerId(customerId)
                .collectList()
                .map(AccountSearchResponse::new)
                .doOnNext(res -> System.out.println(res.accounts().size()))
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Account>> createAccount(@RequestBody CreateAccountRequest createAccountRequest){
        return accountService.createAccount(createAccountRequest)
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteAccount(@PathVariable("id") UUID accountId){
        return accountService.deleteAccount(accountId)
                .then(Mono.just(toResponseEntity(accountId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Account>> getAccount(@PathVariable("id") UUID accountId){
        System.out.println("Received request!");
        return accountService
                .getAccount(accountId)
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
