package com.alphbank.corepaymentservice.service;

import com.alphbank.corepaymentservice.rest.model.CreatePaymentRequest;
import com.alphbank.corepaymentservice.rest.model.Payment;
import com.alphbank.corepaymentservice.rest.model.PaymentSearchRestResponse;
import com.alphbank.corepaymentservice.rest.model.PaymentSearchResult;
import com.alphbank.corepaymentservice.service.client.coreaccountservice.CoreAccountServiceInternalClient;
import com.alphbank.corepaymentservice.service.client.coreaccountservice.model.AccountBalanceUpdateRequest;
import com.alphbank.corepaymentservice.service.error.PaymentNotFoundException;
import com.alphbank.corepaymentservice.service.repository.PaymentRepository;
import com.alphbank.corepaymentservice.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
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

    public Flux<PaymentSearchResult> findAllPaymentsOptionalFilters(UUID fromAccountId, String recipientIban) {
        return findAllPaymentsByFromAccountId(fromAccountId)
                .concatWith(findAllPaymentsByRecipientIban(recipientIban));
    }

    private Flux<PaymentSearchResult> findAllPaymentsByFromAccountId(UUID fromAccountId){
        if(fromAccountId == null){
            return Flux.empty();
        }
         return paymentRepository.findAllPaymentsByFromAccountId(fromAccountId)
                 .map(paymentEntity -> convertToSearchResponse(paymentEntity, false));
    }

    private Flux<PaymentSearchResult> findAllPaymentsByRecipientIban(String recipientIban){
        if(recipientIban == null){
            return Flux.empty();
        }
        return paymentRepository.findAllPaymentsByRecipientIban(recipientIban)
                .map(paymentEntity -> convertToSearchResponse(paymentEntity, true));
    }

    @Transactional
    public Mono<Payment> createPayment(CreatePaymentRequest createPaymentRequest) {
        // TODO: Add recipientAccountId as an async lookup to account-service - search based on IBAN?
        // TODO: Run scheduleExecution on the persisted payment
        PaymentEntity paymentEntity = PaymentEntity.from(createPaymentRequest);
        return paymentRepository.save(paymentEntity)
                .map(this::convertToRestModel);
                //.flatMap(this::scheduleExecution)
                //.then(Mono.just(convertToRestModel(paymentEntity)));
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
                        paymentEntity.getFromAccountId(),
                        paymentEntity.getRecipientIban(),
                        paymentEntity.getPaymentId());
        coreAccountServiceInternalClient.updateBalances(accountBalanceUpdateRequest)
                .then(Mono.just(paymentEntity.withExecuted(true)))
                .flatMap(paymentRepository::save)
                .subscribe();
    }

    private Payment convertToRestModel(PaymentEntity paymentEntity) {
        return new Payment(paymentEntity.getPaymentId(),
                paymentEntity.getFromCustomerId(),
                paymentEntity.getFromAccountId(),
                paymentEntity.isExecuted(),
                Money.of(paymentEntity.getRemittanceAmount(), paymentEntity.getRemittanceCurrency()),
                paymentEntity.getRecipientIban(),
                paymentEntity.getRecipientAccountId(),
                paymentEntity.getMessageToSelf(),
                paymentEntity.getMessageToRecipient(),
                paymentEntity.getExecutionDateTime(),
                paymentEntity.getScheduledDateTime());
    }

    // Good candidate for compositional design, but realistically we will only ever have two options.
    // Keep it simple with a boolean instead
    private PaymentSearchResult convertToSearchResponse(PaymentEntity paymentEntity, boolean recipientPointOfView) {
        String message = recipientPointOfView ? paymentEntity.getMessageToRecipient() : paymentEntity.getMessageToSelf();
        BigDecimal remittanceAmount = recipientPointOfView ? paymentEntity.getRemittanceAmount() : paymentEntity.getRemittanceAmount().negate();

        return new PaymentSearchResult(paymentEntity.getPaymentId(),
                paymentEntity.getFromCustomerId(),
                paymentEntity.getFromAccountId(),
                paymentEntity.isExecuted(),
                Money.of(remittanceAmount, paymentEntity.getRemittanceCurrency()),
                paymentEntity.getRecipientIban(),
                paymentEntity.getRecipientAccountId(),
                message,
                paymentEntity.getScheduledDateTime());
    }
}
