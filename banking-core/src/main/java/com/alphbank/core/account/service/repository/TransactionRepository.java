package com.alphbank.core.account.service.repository;

import com.alphbank.core.account.service.repository.model.TransactionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface TransactionRepository extends R2dbcRepository<TransactionEntity, UUID> {

    @Query("""
                SELECT t.newBalance
                FROM Transaction t
                WHERE t.accountId = :accountId
                ORDER BY t.executionDate DESC
                LIMIT 1
            """)
    Mono<BigDecimal> getNewBalanceByAccountIdOrderByExecutionDateTimeDesc(UUID accountId);

    Flux<TransactionEntity> findAllByAccountIdOrderByExecutionDateTimeDesc(UUID accountId);
}
