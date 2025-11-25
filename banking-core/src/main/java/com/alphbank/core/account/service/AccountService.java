package com.alphbank.core.account.service;

import com.alphbank.core.account.service.error.AccountNotFoundException;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.model.AccountTransferRequest;
import com.alphbank.core.account.service.model.LoanPayoutRequest;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import com.alphbank.core.customer.rest.error.model.CustomerNotFoundException;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public Mono<Account> createAccount(Account account) {
        return customerRepository.findById(account.getCustomerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(account.getCustomerId())))
                .map(customerEntity -> Locale.of(customerEntity.getLanguage(), customerEntity.getCountry()))
                .map(locale -> AccountEntity.from(account, locale))
                .flatMap(accountRepository::save)
                .map(AccountEntity::toModel);
    }

    public Flux<Account> getAllAccountsByCustomerId(String customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .map(AccountEntity::toModel);
    }

    public Mono<Account> getAccount(UUID id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .map(AccountEntity::toModel);
    }

    public Mono<Void> deleteAccount(UUID id) {
        return accountRepository.deleteById(id);
    }


    // TODO:
    @Transactional
    public Mono<Void> transferBetweenAccounts(AccountTransferRequest accountTransferRequest) {
        MonetaryAmount amount = accountTransferRequest.amount();
        return reduceBalance(accountTransferRequest.debtorAccountId(), amount)
                .flatMap(ignored -> increaseBalance(accountTransferRequest.recipientIban(), amount))
                .then();
    }

    public Mono<Void> payoutLoan(LoanPayoutRequest loanPayoutRequest) {
        return increaseBalance(loanPayoutRequest.debtorAccountId(), loanPayoutRequest.principal())
                .then();
    }

    private Mono<AccountEntity> reduceBalance(UUID debtorAccountId, MonetaryAmount amount) {
        return accountRepository.findById(debtorAccountId)
                .map(accountEntity -> {
                    BigDecimal balanceDelta = amount.getNumber().numberValue(BigDecimal.class);
                    accountEntity.setBalance(accountEntity.getBalance().subtract(balanceDelta));
                    return accountEntity;
                })
                .flatMap(accountRepository::save);
    }

    private Mono<AccountEntity> increaseBalance(String recipientIban, MonetaryAmount amount) {
        return accountRepository.findByIban(recipientIban)
                .flatMap(accountEntity -> increaseBalance(accountEntity, amount));
    }

    private Mono<AccountEntity> increaseBalance(UUID recipientAccountId, MonetaryAmount amount) {
        return accountRepository.findById(recipientAccountId)
                .flatMap(accountEntity -> increaseBalance(accountEntity, amount));
    }

    private Mono<AccountEntity> increaseBalance(AccountEntity accountEntity, MonetaryAmount amount) {
        BigDecimal balanceDelta = amount.getNumber().numberValue(BigDecimal.class);
        accountEntity.setBalance(accountEntity.getBalance().add(balanceDelta));
        return accountRepository.save(accountEntity);
    }
}
