package com.alphbank.core.account.service;

import com.alphbank.core.account.rest.error.model.AccountNotFoundByIbanException;
import com.alphbank.core.account.rest.error.model.AccountNotFoundException;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.model.Transaction;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.TransactionRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import com.alphbank.core.account.service.repository.model.TransactionEntity;
import com.alphbank.core.customer.rest.error.model.CustomerNotFoundException;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.shared.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final TransactionService transactionService;

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public Mono<Account> createAccount(Account account) {
        return customerRepository.findById(account.getCustomerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(account.getCustomerId())))
                .map(customerEntity -> Locale.of(customerEntity.getLanguage(), customerEntity.getCountry()))
                .map(locale -> AccountEntity.from(account, locale))
                .flatMap(accountRepository::save)
                .map(savedEntity -> Account.from(savedEntity, savedEntity.getInitialBalance()));
    }

    public Mono<Account> getAccount(UUID id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .zipWith(getCurrentBalance(id))
                .map(TupleUtils.function(Account::from));
    }

    public Mono<Void> deleteAccount(UUID id) {
        return accountRepository.deleteById(id);
    }

    public Flux<Account> getAllAccountsByCustomerId(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .flatMap(accountEntity -> getCurrentBalance(accountEntity.getId())
                        .map(balance -> Account.from(accountEntity, balance)));
    }

    public Flux<Transaction> getTransactionsOrderedByExecutionDate(UUID accountId) {
        return transactionService.getTransactionsOnAccountOrdered(accountId);
    }

    public Mono<Account> getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .switchIfEmpty(Mono.error(new AccountNotFoundByIbanException(iban)))
                .zipWhen(accountEntity -> getCurrentBalance(accountEntity.getId()))
                .map(TupleUtils.function(Account::from));
    }

    public Mono<BigDecimal> getCurrentBalance(UUID accountId) {
        return transactionService.getBalanceOfAccount(accountId);
    }

}
