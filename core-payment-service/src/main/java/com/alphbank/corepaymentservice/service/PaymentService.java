package com.alphbank.corepaymentservice.service;

import com.alphbank.corepaymentservice.rest.model.CreatePaymentRequest;
import com.alphbank.corepaymentservice.rest.model.Payment;
import com.alphbank.corepaymentservice.service.client.coreaccountservice.CoreAccountServiceInternalClient;
import com.alphbank.corepaymentservice.service.client.coreaccountservice.model.AccountBalanceUpdateRequest;
import com.alphbank.corepaymentservice.service.error.PaymentNotFoundException;
import com.alphbank.corepaymentservice.service.repository.PaymentRepository;
import com.alphbank.corepaymentservice.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.money.MonetaryAmount;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TaskScheduler taskScheduler;
    private final CoreAccountServiceInternalClient coreAccountServiceInternalClient;

    // https://springdoc.org/faq.html
    /*
    static {
        SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, org.springdoc.core.converters.models.MonetaryAmount.class);
    }
    */

    public Flux<Payment> findAllPaymentsByCustomerIdAndAccountId(UUID customerId, UUID accountId) {
        return Mono.justOrEmpty(customerId)
                .flatMapMany(paymentRepository::findAllPaymentsByCustomerId)
                .filter(paymentEntity -> accountId == null || paymentEntity.getAccountId() == accountId)
                .switchIfEmpty(paymentRepository.findAllPaymentsByAccountId(accountId))
                .map(this::convertToRestModel);
    }

    @Transactional
    public Mono<Payment> createPayment(CreatePaymentRequest createPaymentRequest) {
        PaymentEntity paymentEntity = PaymentEntity.from(createPaymentRequest);
        return paymentRepository.save(paymentEntity)
                .flatMap(this::scheduleExecution)
                .then(Mono.just(convertToRestModel(paymentEntity)));
    }

    public Mono<Payment> getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .map(this::convertToRestModel);
    }

    public Mono<Void> deletePayment(UUID paymentId) {
        return paymentRepository.deleteById(paymentId);
    }

    private Mono<Void> scheduleExecution(PaymentEntity paymentEntity) {
        return Mono.justOrEmpty(paymentEntity.getExecutionDateTime())
                .switchIfEmpty(Mono.just(LocalDateTime.now()))
                .map(localDateTime -> localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant())
                // Assume that the transfer action is persisted in this transaction
                // Using Spring scheduler for simplicity
                .map(payoutDateTime -> taskScheduler.schedule(new TransferMoneyAsyncTask(() -> transfer(paymentEntity)), payoutDateTime))
                .then();
    }

    // Figure out a way to handle if the executed -> true call goes wrong, or if we never get a response from coreAccountService
    // Maybe if this was an actual persisted async task, we would mark it as "finished" at the end, and we could have a daily job that checks all async tasks that
    // are unfinished but were supposed to be executed?
    public void transfer(PaymentEntity paymentEntity) {
        AccountBalanceUpdateRequest accountBalanceUpdateRequest = new AccountBalanceUpdateRequest
                (Money.of(paymentEntity.getRemittanceAmount(), paymentEntity.getRemittanceCurrency()),
                        paymentEntity.getAccountId(),
                        paymentEntity.getRecipientIban(),
                        paymentEntity.getPaymentId());
        coreAccountServiceInternalClient.updateBalances(accountBalanceUpdateRequest)
                .then(Mono.just(paymentEntity.withExecuted(true)))
                .flatMap(paymentRepository::save)
                .subscribe();
    }

    private Payment convertToRestModel(PaymentEntity paymentEntity) {
        return new Payment(paymentEntity.getPaymentId(),
                paymentEntity.getCustomerId(),
                paymentEntity.getAccountId(),
                paymentEntity.isExecuted(),
                Money.of(paymentEntity.getRemittanceAmount(), paymentEntity.getRemittanceCurrency()),
                paymentEntity.getRecipientIban(),
                paymentEntity.getMessageToSelf());
    }
}
