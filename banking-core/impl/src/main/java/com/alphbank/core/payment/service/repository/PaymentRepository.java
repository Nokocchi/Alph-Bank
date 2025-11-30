package com.alphbank.core.payment.service.repository;

import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PaymentRepository extends R2dbcRepository<PaymentEntity, UUID> {

    Flux<PaymentEntity> findAllPaymentsByFromAccountId(UUID accountId);
}
