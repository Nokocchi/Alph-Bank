package com.alphbank.loanapplicationservice.service.repository;

import com.alphbank.loanapplicationservice.service.repository.model.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends R2dbcRepository<LoanApplicationEntity, UUID> {

    Flux<LoanApplicationEntity> findAllLoanApplicationsByCustomerId(UUID customerId);

    Flux<LoanApplicationEntity> findAllLoanApplicationsByAccountId(UUID accountId);

    Mono<LoanApplicationEntity> findBySigningSessionId(UUID signingSessionId);
}
