package com.alphbank.core.account.service;

import com.alphbank.core.account.rest.error.model.AccountNotFoundException;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.model.Transaction;
import com.alphbank.core.account.service.model.TransactionCreatedByType;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.TransactionRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import com.alphbank.core.account.service.repository.model.TransactionEntity;
import com.alphbank.core.customer.rest.error.model.CustomerNotFoundException;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public Mono<Account> createAccount(Account account) {
        return customerRepository.findById(account.getCustomerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(account.getCustomerId())))
                .map(customerEntity -> Locale.of(customerEntity.getLanguage(), customerEntity.getCountry()))
                .map(locale -> AccountEntity.from(account, locale))
                .flatMap(accountRepository::save)
                .flatMap(this::toModel);
    }

    public Mono<Account> getAccount(UUID id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .flatMap(this::toModel);
    }

    public Mono<Void> deleteAccount(UUID id) {
        return accountRepository.deleteById(id);
    }

    public Flux<Account> getAllAccountsByCustomerId(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .flatMap(this::toModel);
    }

    public Flux<Transaction> getTransactionsOrderedByExecutionDate(UUID accountId) {
        return transactionRepository.findAllByAccountIdOrderByExecutionDateTimeDesc(accountId)
                .map(TransactionEntity::toModel);
    }

    // Double bookkeeping where account balance changes can only be done by creating transactions from payments
    public Mono<Void> executePaymentOnAccounts(PaymentEntity paymentEntity) {
        TransactionEntity.TransactionEntityBuilder transactionEntityBuilder = TransactionEntity.builder()
                .createdFromId(paymentEntity.getId())
                .createdFromType(TransactionCreatedByType.PAYMENT)
                .executionDateTime(LocalDateTime.now())
                .amount(paymentEntity.getMonetaryValue())
                .currencyCode(paymentEntity.getCurrency());

        // Debtor perspective
        Mono<TransactionEntity> debtorTransaction = getCurrentBalance(paymentEntity.getFromAccountId())
                .map(currentBalance -> transactionEntityBuilder
                        .accountId(paymentEntity.getFromAccountId())
                        .message(paymentEntity.getMessageToSelf())
                        .newBalance(currentBalance.subtract(paymentEntity.getMonetaryValue()))
                        .build());

        // Creditor perspective
        Mono<TransactionEntity> creditorTransaction = Mono.just(paymentEntity.getRecipientAccountId())
                .switchIfEmpty(accountRepository.findByIban(paymentEntity.getRecipientIban()).map(AccountEntity::getId))
                .zipWhen(this::getCurrentBalance)
                .map(TupleUtils.function((accountId, currentBalance) -> transactionEntityBuilder
                        .accountId(accountId)
                        .message(paymentEntity.getMessageToRecipient())
                        .newBalance(currentBalance.add(paymentEntity.getMonetaryValue()))
                        .build()));

        return Mono.when(
                debtorTransaction.flatMap(transactionRepository::save),
                creditorTransaction.flatMap(transactionRepository::save)
        );
    }

    private Mono<Account> toModel(AccountEntity accountEntity) {
        return getCurrentBalance(accountEntity.getId())
                .map(currentBalance -> Account.from(accountEntity, currentBalance));
    }

    private Mono<BigDecimal> getCurrentBalance(UUID accountId) {
        return transactionRepository.getNewBalanceByAccountIdOrderByExecutionDateTimeDesc(accountId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }
}
