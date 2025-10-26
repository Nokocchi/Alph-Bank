package com.alphbank.core.account.service;

import com.alphbank.core.account.rest.model.Account;
import com.alphbank.core.account.rest.model.CreateAccountRequest;
import com.alphbank.core.account.service.error.AccountNotFoundException;
import com.alphbank.core.account.service.model.AccountTransferRequest;
import com.alphbank.core.account.service.model.LoanPayoutRequest;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Flux<Account> getAllAccountsByCustomerId(String customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .map(this::convertToRestModel);
    }

    public Mono<Account> createAccount(CreateAccountRequest createAccountRequest) {
        AccountEntity accountEntity = AccountEntity.from(createAccountRequest);
        return accountRepository.save(accountEntity)
                .map(this::convertToRestModel);
    }

    public Mono<Account> getAccount(UUID id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .map(this::convertToRestModel);
    }

    public Mono<Void> deleteAccount(UUID id) {
        return accountRepository.deleteById(id);
    }

    private Account convertToRestModel(AccountEntity accountEntity) {
        Money balance = Money.of(accountEntity.getBalance(), accountEntity.getCurrencyCode());
        return new Account(accountEntity.getAccountId(), accountEntity.getAccountName(), balance, accountEntity.getIban());
    }

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
