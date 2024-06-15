package com.alphbank.corepaymentservice.service.repository;

import com.alphbank.corepaymentservice.service.repository.model.PaymentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaymentRepository extends R2dbcRepository<PaymentEntity, UUID> {

    Flux<PaymentEntity> findAllPaymentsByCustomerId(UUID customerId);

    Flux<PaymentEntity> findAllPaymentsByAccountId(UUID accountId);
}
