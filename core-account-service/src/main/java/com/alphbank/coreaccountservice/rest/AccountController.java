package com.alphbank.coreaccountservice.rest;

import com.alphbank.commons.impl.JsonLog;
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
    private final JsonLog jsonLog;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<AccountSearchResponse>> searchAccounts(@RequestParam(name = "customer_id") String customerId){
        log.info("Search accounts by customerId {}", customerId);
        return accountService.getAllAccountsByCustomerId(customerId)
                .collectList()
                .map(AccountSearchResponse::new)
                .doOnNext(response -> log.info("Returning accounts {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching accounts", e))
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Account>> createAccount(@RequestBody CreateAccountRequest createAccountRequest){
        log.info("Creating account {}", jsonLog.format(createAccountRequest));
        return accountService.createAccount(createAccountRequest)
                .doOnNext(response -> log.info("Returning newly created account {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating account", e))
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteAccount(@PathVariable("id") UUID accountId){
        log.info("Deleting account with accountId {}", accountId);
        return accountService.deleteAccount(accountId)
                .doOnSuccess(response -> log.info("Successfully deleted account with id {}", accountId))
                .doOnError(e -> log.error("Error deleting account with id " + accountId, e))
                .then(Mono.just(toResponseEntity(accountId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Account>> getAccount(@PathVariable("id") UUID accountId){
        log.info("Getting account with accountId {}", accountId);
        return accountService
                .getAccount(accountId)
                .doOnSuccess(response -> log.info("Returning account {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting account with id " + accountId, e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
