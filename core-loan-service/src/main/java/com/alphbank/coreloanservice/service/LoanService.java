package com.alphbank.coreloanservice.service;

import com.alphbank.coreloanservice.rest.model.CreateLoanRequest;
import com.alphbank.coreloanservice.rest.model.Loan;
import com.alphbank.coreloanservice.service.client.coreaccountservice.CoreAccountServiceInternalClient;
import com.alphbank.coreloanservice.service.client.coreaccountservice.model.PayoutLoanRequest;
import com.alphbank.coreloanservice.service.error.InvalidLoanSearchException;
import com.alphbank.coreloanservice.service.error.LoanNotFoundException;
import com.alphbank.coreloanservice.service.amqp.RabbitMQService;
import com.alphbank.coreloanservice.service.repository.LoanRepository;
import com.alphbank.coreloanservice.service.repository.model.LoanEntity;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final TaskScheduler taskScheduler;
    private final RabbitMQService rabbitMQService;
    private final CoreAccountServiceInternalClient coreAccountServiceInternalClient;

    public Flux<Loan> findAllLoansByCustomerIdOrAccountId(UUID customerId, UUID accountId) {
        if (customerId == null && accountId == null) {
            throw new InvalidLoanSearchException("Both customerId and accountId are null.");
        }
        if (customerId != null && accountId != null) {
            throw new InvalidLoanSearchException("Both customerId and accountId are set.");
        }
        return Mono.justOrEmpty(customerId)
                .flatMapMany(loanRepository::findAllLoansByCustomerId)
                .switchIfEmpty(loanRepository.findAllLoansByAccountId(accountId))
                .map(this::convertToRestModel);
    }

    @Transactional
    public Mono<Loan> createLoan(CreateLoanRequest createLoanRequest) {
        return loanRepository.save(LoanEntity.from(createLoanRequest))
                .map(entity -> {
                    scheduleExecution(entity);
                    return convertToRestModel(entity);
                });
    }

    public Mono<Loan> getLoan(UUID loanId) {
        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new LoanNotFoundException()))
                .map(this::convertToRestModel);
    }

    public Mono<Void> deleteLoan(UUID loanId) {
        return loanRepository.deleteById(loanId);
    }

    private void scheduleExecution(LoanEntity loanEntity) {
        // Assume that the transfer async task is persisted in this transaction
        // Using Spring scheduler for simplicity
        Instant executionTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant();
        taskScheduler.schedule(new PayOutLoanAsyncTask(() -> payoutLoan(loanEntity)), executionTime);
    }

    // Figure out a way to handle if the withPayoutDateTime call goes wrong, or if we never get a response from coreAccountService. Or if rabbitMq send goes wrong
    // Maybe if this was an actual persisted async task, we would mark it as "finished" at the end, and we could have a daily job that checks all async tasks that
    // are unfinished but were supposed to be executed?
    private void payoutLoan(LoanEntity loanEntity) {
        PayoutLoanRequest accountBalanceUpdateRequest = new PayoutLoanRequest
                (Money.of(loanEntity.getPrincipal(), loanEntity.getCurrency()),
                        loanEntity.getAccountId(),
                        loanEntity.getLoanId());

        // Send to GovernmentReportingTransferService via RabbitMQ
        coreAccountServiceInternalClient.payoutLoan(accountBalanceUpdateRequest)
                .then(loanRepository.save(loanEntity.withPayoutDateTime(LocalDateTime.now())))
                .flatMap(entity -> rabbitMQService.send(accountBalanceUpdateRequest))
                .subscribe();
    }

    private Loan convertToRestModel(LoanEntity loanEntity) {
        return new Loan(loanEntity.getLoanId(),
                loanEntity.getCustomerId(),
                loanEntity.getAccountId(),
                Money.of(loanEntity.getPrincipal(), loanEntity.getCurrency()),
                loanEntity.getFixedRateInterestAPR(),
                loanEntity.getLoanPeriodMonths(),
                loanEntity.getPayoutDateTime());
    }
}
