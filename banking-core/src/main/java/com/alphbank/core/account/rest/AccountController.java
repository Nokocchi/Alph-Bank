package com.alphbank.core.account.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.account.rest.model.Account;
import com.alphbank.core.account.rest.model.AccountSearchResponse;
import com.alphbank.core.account.rest.model.CreateAccountRequest;
import com.alphbank.core.account.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "Account", description = "Description")
public class AccountController {

    private final AccountService accountService;
    private final JsonLog jsonLog;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AccountSearchResponse> searchAccounts(@RequestParam(name = "customer-id") String customerId) {
        log.info("Search accounts by customerId {}", customerId);
        return accountService.getAllAccountsByCustomerId(customerId)
                .collectList()
                .map(AccountSearchResponse::new)
                .doOnNext(response -> log.info("Returning accounts {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching accounts", e));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Account> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        log.info("Creating account {}", jsonLog.format(createAccountRequest));
        return accountService.createAccount(createAccountRequest)
                .doOnNext(response -> log.info("Returning newly created account {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating account", e));

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UUID> deleteAccount(@PathVariable("id") UUID accountId) {
        log.info("Deleting account with accountId {}", accountId);
        return accountService.deleteAccount(accountId)
                .doOnSuccess(response -> log.info("Successfully deleted account with id {}", accountId))
                .doOnError(e -> log.error("Error deleting account with id " + accountId, e))
                .thenReturn(accountId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Account> getAccount(@PathVariable("id") UUID accountId) {
        log.info("Getting account with accountId {}", accountId);
        return accountService
                .getAccount(accountId)
                .doOnSuccess(response -> log.info("Returning account {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting account with id " + accountId, e));
    }

}
