package com.alphbank.core.payment.service;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import com.alphbank.core.payment.rest.error.model.PaymentNotFoundException;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @Transactional
    public Mono<Payment> createPayment(Payment payment) {
        return Mono.just(payment)
                .map(PaymentEntity::from)
                .flatMap(paymentRepository::save)
                .flatMap(this::scheduleOrExecutePayment)
                .map(Payment::fromEntity);
    }

    public Mono<Payment> getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .map(Payment::fromEntity);
    }

    public Mono<Void> deletePayment(UUID paymentId) {
        return paymentRepository.deleteById(paymentId);
    }

    public Flux<Payment> getPendingPaymentsByCustomerId(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .map(AccountEntity::getId)
                .flatMap(paymentRepository::findAllPaymentsByFromAccountId)
                .filter(payment -> payment.getExecutionDateTime() == null)
                .map(Payment::fromEntity);
    }

    private Mono<PaymentEntity> scheduleOrExecutePayment(PaymentEntity paymentEntity) {
        if (paymentEntity.getScheduledDateTime() != null && paymentEntity.getScheduledDateTime().isAfter(LocalDateTime.now())) {
            // Payment will be picked up by scheduled job
            return Mono.just(paymentEntity);
        }

        return accountService.executePaymentOnAccounts(paymentEntity)
                .thenReturn(paymentEntity);
    }

}
