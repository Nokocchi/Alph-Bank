package com.alphbank.core.shared;

import com.alphbank.commons.impl.Utils;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.model.Transaction;
import com.alphbank.core.account.service.repository.TransactionRepository;
import com.alphbank.core.account.service.repository.model.TransactionEntity;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Double entry bookkeeping where account balance changes can only be done by creating 2x transactions from payments
    public Mono<Void> executePaymentOnAccounts(Account creditorAccount, Account debtorAccount, PaymentEntity paymentEntity) {

        boolean fromAccountIdMatches = paymentEntity.getFromAccountId().equals(debtorAccount.getId());
        boolean recipientAccountIBanMatches = paymentEntity.getRecipientIban().equals(creditorAccount.getIban());
        if (!fromAccountIdMatches || !recipientAccountIBanMatches) {
            throw new IllegalStateException("");
        }

        // Debtor perspective
        TransactionEntity debtorTransaction = TransactionEntity.createBuilder(paymentEntity)
                .accountId(debtorAccount.getId())
                .amount(paymentEntity.getMonetaryValue().negate())
                .message(paymentEntity.getMessageToSelf())
                .newBalance(Utils.getAmount(debtorAccount.getBalance()).subtract(paymentEntity.getMonetaryValue()))
                .build();

        // Creditor perspective
        TransactionEntity creditorTransaction = TransactionEntity.createBuilder(paymentEntity)
                .accountId(creditorAccount.getId())
                .amount(paymentEntity.getMonetaryValue())
                .message(paymentEntity.getMessageToRecipient())
                .newBalance(Utils.getAmount(creditorAccount.getBalance()).add(paymentEntity.getMonetaryValue()))
                .build();

        return Mono.when(
                transactionRepository.save(creditorTransaction),
                transactionRepository.save(debtorTransaction)
        );
    }

    public Mono<BigDecimal> getBalanceOfAccount(UUID accountId) {
        return transactionRepository.getNewBalanceByAccountIdOrderByExecutionDateTimeDesc(accountId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    public Flux<Transaction> getTransactionsOnAccountOrdered(UUID accountId) {
        return transactionRepository.findAllByAccountIdOrderByExecutionDateTimeDesc(accountId)
                .map(TransactionEntity::toModel);
    }
}
