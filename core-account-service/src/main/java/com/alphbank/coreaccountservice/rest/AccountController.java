package com.alphbank.coreaccountservice.rest;

import com.alphbank.coreaccountservice.rest.model.Account;
import com.alphbank.coreaccountservice.rest.model.AccountSearchResponse;
import com.alphbank.coreaccountservice.rest.model.CreateAccountRequest;
import com.alphbank.coreaccountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<AccountSearchResponse>> searchAccounts(@RequestParam(name = "customer_id") String customerId){
        return accountService.getAllAccountsByCustomerId(customerId)
                .collectList()
                .map(AccountSearchResponse::new)
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
