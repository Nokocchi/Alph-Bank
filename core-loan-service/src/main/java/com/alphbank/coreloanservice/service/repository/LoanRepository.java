package com.alphbank.coreloanservice.service.repository;

import com.alphbank.coreloanservice.service.repository.model.LoanEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface LoanRepository extends R2dbcRepository<LoanEntity, UUID> {

    Flux<LoanEntity> findAllLoansByCustomerId(UUID customerId);

    Flux<LoanEntity> findAllLoansByAccountId(UUID accountId);

}
