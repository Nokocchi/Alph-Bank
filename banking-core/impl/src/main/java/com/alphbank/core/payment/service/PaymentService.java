package com.alphbank.core.payment.service;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.model.AccountEntity;
import com.alphbank.core.payment.rest.error.model.PaymentNotFoundException;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.payment.service.model.PeriodicPayment;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.PeriodicPaymentRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import com.alphbank.core.payment.service.repository.model.PeriodicPaymentEntity;
import com.alphbank.core.rest.model.PeriodicPaymentFreuqencyDTO;
import com.alphbank.core.shared.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountService accountService;
    private final TransactionService transactionService;

    private final PaymentRepository paymentRepository;
    private final PeriodicPaymentRepository periodicPaymentRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public Mono<Payment> createPayment(Payment payment) {
        return accountService.getAccountByIban(payment.getRecipientIban())
                .map(recipientAccount -> payment.withRecipientAccountId(recipientAccount.getId()))
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

    @Transactional
    public Mono<PeriodicPayment> createPeriodicPayment(PeriodicPayment periodicPayment) {
        return accountService.getAccountByIban(periodicPayment.getRecipientIban())
                .map(recipientAccount -> periodicPayment.withRecipientAccountId(recipientAccount.getId()))
                .map(PeriodicPaymentEntity::from)
                .flatMap(periodicPaymentRepository::save)
                .flatMap(this::handlePeriodicPayment)
                .map(PeriodicPayment::fromEntity);
    }

    public Mono<PeriodicPayment> getPeriodicPayment(UUID periodicPaymentId) {
        return periodicPaymentRepository.findById(periodicPaymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(periodicPaymentId)))
                .map(PeriodicPayment::fromEntity);
    }

    public Mono<Void> deletePeriodicPayment(UUID periodicPaymentId) {
        return periodicPaymentRepository.deleteById(periodicPaymentId);
    }

    public Flux<PeriodicPayment> getActivePeriodicPaymentsByCustomerId(UUID customerId) {
        return accountRepository.findAllByCustomerId(customerId)
                .map(AccountEntity::getId)
                .flatMap(periodicPaymentRepository::findAllPeriodicPaymentsByFromAccountId)
                .filter(PeriodicPaymentEntity::isActive)
                .map(PeriodicPayment::fromEntity);
    }

    private Mono<PeriodicPaymentEntity> handlePeriodicPayment(PeriodicPaymentEntity periodicPaymentEntity) {
        LocalDateTime startDate = periodicPaymentEntity.getStartDate();
        LocalDateTime scheduledDate = null;
        if (startDate != null && startDate.isAfter(LocalDateTime.now())) {
            scheduledDate = startDate;
        }

        PaymentEntity paymentEntity = PaymentEntity.createFrom(periodicPaymentEntity, scheduledDate);

        return scheduleOrExecutePayment(paymentEntity)
                .thenReturn(periodicPaymentEntity);
    }

    private Mono<Void> scheduleNextPaymentByPeriodicPaymentEntity(PeriodicPaymentEntity periodicPaymentEntity) {
        LocalDateTime endDate = periodicPaymentEntity.getEndDate();
        PeriodicPaymentFreuqencyDTO frequency = periodicPaymentEntity.getFrequency();

        LocalDateTime scheduledDateTime = switch (frequency) {
            case EVERY_10_SECONDS -> LocalDateTime.now().plusSeconds(10);
            case DAILY -> LocalDateTime.now().plusDays(1);
            case MONTHLY -> LocalDateTime.now().plusMonths(1);
            case ANNUAL -> LocalDateTime.now().plusYears(1);
            // TODO: Maybe cancel the periodic payment?
        };

        if (scheduledDateTime.isBefore(endDate)) {
            return Mono.empty();
        }

        PaymentEntity paymentEntity = PaymentEntity.createFrom(periodicPaymentEntity, scheduledDateTime);
        return scheduleOrExecutePayment(paymentEntity)
                .then();
    }

    public Mono<PaymentEntity> scheduleOrExecutePayment(PaymentEntity paymentEntity) {
        if (paymentEntity.getScheduledDateTime() != null && paymentEntity.getScheduledDateTime().isAfter(LocalDateTime.now())) {
            // Payment will be picked up by scheduled job
            return Mono.just(paymentEntity);
        }

        return Mono.zip(accountService.getAccountByIban(paymentEntity.getRecipientIban()),
                        accountService.getAccount(paymentEntity.getFromAccountId()),
                        Mono.just(paymentEntity))
                .flatMap(TupleUtils.function(transactionService::executePaymentOnAccounts))
                .then(scheduleNextPaymentByPeriodicPaymentId(paymentEntity.getPeriodicPaymentId()))
                .thenReturn(paymentEntity);
    }

    private Mono<Void> scheduleNextPaymentByPeriodicPaymentId(UUID periodicPaymentId) {
        if (periodicPaymentId == null) {
            return Mono.empty();
        }

        return periodicPaymentRepository.findById(periodicPaymentId)
                .flatMap(this::scheduleNextPaymentByPeriodicPaymentEntity);
    }

}
