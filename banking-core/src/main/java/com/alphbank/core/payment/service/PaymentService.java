package com.alphbank.core.payment.service;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.model.AccountTransferRequest;
import com.alphbank.core.payment.rest.model.PaymentSearchResult;
import com.alphbank.core.payment.service.error.PaymentNotFoundException;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import com.alphbank.core.rest.model.PaymentSearchResultDTO;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TaskScheduler taskScheduler;
    private final AccountService accountService;

    // https://springdoc.org/faq.html
    /*
    static {
        SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, org.springdoc.core.converters.models.MonetaryAmount.class);
    }
    */

    public Flux<PaymentSearchResultDTO> findAllPaymentsOptionalFilters(UUID fromAccountId, String recipientIban) {
        return findAllPaymentsByFromAccountId(fromAccountId)
                .concatWith(findAllPaymentsByRecipientIban(recipientIban));
    }

    private Flux<PaymentSearchResultDTO> findAllPaymentsByFromAccountId(UUID fromAccountId) {
        if (fromAccountId == null) {
            return Flux.empty();
        }
        return paymentRepository.findAllPaymentsByFromAccountId(fromAccountId)
                .map(paymentEntity -> convertToSearchResponse(paymentEntity, false));
    }

    private Flux<PaymentSearchResultDTO> findAllPaymentsByRecipientIban(String recipientIban) {
        if (recipientIban == null) {
            return Flux.empty();
        }
        return paymentRepository.findAllPaymentsByRecipientIban(recipientIban)
                .map(paymentEntity -> convertToSearchResponse(paymentEntity, true));
    }

    @Transactional
    public Mono<PaymentDTO> createPayment(CreatePaymentRequestDTO createPaymentRequest) {
        return paymentRepository.save(PaymentEntity.from(createPaymentRequest))
                .map(entity -> {
                    //scheduleExecution(entity);
                    return convertToRestModel(entity);
                });
    }

    public Mono<PaymentDTO> getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .map(this::convertToRestModel);
    }

    public Mono<Void> deletePayment(UUID paymentId) {
        return paymentRepository.deleteById(paymentId);
    }

    private void scheduleExecution(PaymentEntity paymentEntity) {
        LocalDateTime scheduledTime = Optional.ofNullable(paymentEntity.getScheduledDateTime()).orElse(LocalDateTime.now());
        Instant executionTime = scheduledTime.atZone(TimeZone.getDefault().toZoneId()).toInstant();
        // Assume that the transfer action is persisted in this transaction
        // Using Spring scheduler for simplicity
        System.out.println("Scheduled payment at " + executionTime);
        taskScheduler.schedule(new TransferMoneyAsyncTask(() -> transfer(paymentEntity)), executionTime);
    }

    // Figure out a way to handle if the executed -> true call goes wrong, or if we never get a response from coreAccountService
    // Maybe if this was an actual persisted async task, we would mark it as "finished" at the end, and we could have a daily job that checks all async tasks that
    // are unfinished but were supposed to be executed?
    public void transfer(PaymentEntity paymentEntity) {
        System.out.println("Payment execution started");
        AccountTransferRequest accountBalanceUpdateRequest = new AccountTransferRequest(
                (Money.of(paymentEntity.getMonetaryValue(), paymentEntity.getCurrency())),
                paymentEntity.getFromAccountId(),
                paymentEntity.getRecipientIban(),
                paymentEntity.getPaymentId());
        accountService.transferBetweenAccounts(accountBalanceUpdateRequest)
                .thenReturn(paymentEntity.withExecutionDateTime(LocalDateTime.now()))
                .flatMap(paymentRepository::save)
                .subscribe();
    }

    private PaymentDTO convertToRestModel(PaymentEntity paymentEntity) {
        return PaymentDTO.builder()
                .paymentId(paymentEntity.getPaymentId())
                .fromCustomerId(paymentEntity.getFromCustomerId())
                .fromAccountId(paymentEntity.getFromAccountId())
                .amount(Money.of(paymentEntity.getMonetaryValue(), paymentEntity.getCurrency()))
                .recipientIban(paymentEntity.getRecipientIban())
                .recipientAccountId(paymentEntity.getRecipientAccountId())
                .messageToSelf(paymentEntity.getMessageToSelf())
                .messageToRecipient(paymentEntity.getMessageToRecipient())
                .scheduledDateTime(paymentEntity.getScheduledDateTime())
                .build();
    }

    // Good candidate for compositional design, but realistically we will only ever have two options.
    // Keep it simple with a boolean instead
    private PaymentSearchResultDTO convertToSearchResponse(PaymentEntity paymentEntity, boolean recipientPointOfView) {
        String message = recipientPointOfView ? paymentEntity.getMessageToRecipient() : paymentEntity.getMessageToSelf();
        BigDecimal amount = recipientPointOfView ? paymentEntity.getMonetaryValue() : paymentEntity.getMonetaryValue().negate();

        return PaymentSearchResultDTO.builder()
                .paymentId(paymentEntity.getPaymentId())
                .fromCustomerId(paymentEntity.getFromCustomerId())
                .fromAccountId(paymentEntity.getFromAccountId())
                .amount(Money.of(amount, paymentEntity.getCurrency()))
                .recipientIban(paymentEntity.getRecipientIban())
                .recipientAccountId(paymentEntity.getRecipientAccountId())
                .message(message)
                .build();
    }
}
