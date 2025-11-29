package com.alphbank.core.account.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.model.Transaction;
import com.alphbank.core.rest.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.AccountApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final AccountService accountService;
    private final JsonLog jsonLog;

    @Override
    public Mono<ResponseEntity<AccountDTO>> createAccount(Mono<CreateAccountRequestDTO> createAccountRequestDTO, ServerWebExchange exchange) {
        return createAccountRequestDTO
                .doOnNext(request -> log.info("Creating account: {}", jsonLog.format(request)))
                .map(Account::from)
                .flatMap(accountService::createAccount)
                .map(Account::toDTO)
                .doOnNext(response -> log.info("Returning newly created account: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating account", e))
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> getAccount(UUID accountId, ServerWebExchange exchange) {
        log.info("Getting account with id: {}", accountId);
        return accountService
                .getAccount(accountId)
                .map(Account::toDTO)
                .doOnNext(response -> log.info("Returning account: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting account with id: {}", accountId, e))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(UUID accountId, ServerWebExchange exchange) {
        log.info("Deleting account with id: {}", accountId);
        return accountService.deleteAccount(accountId)
                .doOnSuccess(ignored -> log.info("Successfully deleted account with id: {}", accountId))
                .doOnError(e -> log.error("Error deleting account with id: {}", accountId, e))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountDTO>>> searchAccounts(UUID customerId, ServerWebExchange exchange) {
        log.info("Search accounts by customerId: {}", customerId);

        Flux<AccountDTO> customers = accountService.getAllAccountsByCustomerId(customerId)
                .map(Account::toDTO);

        return Mono.just(ResponseEntity.ok(customers));
    }

    @Override
    public Mono<ResponseEntity<Flux<TransactionDTO>>> getTransactionsList(UUID accountId, ServerWebExchange exchange) {
        log.info("Search transactions by accountId: {}", accountId);

        Flux<TransactionDTO> transactions = accountService.getTransactionsOrderedByExecutionDate(accountId)
                .map(Transaction::toDTO);

        return Mono.just(ResponseEntity.ok(transactions));
    }
}
