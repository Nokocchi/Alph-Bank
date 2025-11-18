package com.alphbank.payment.service.repository;

import com.alphbank.payment.service.repository.model.PeriodicPaymentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PeriodicPaymentRepository extends R2dbcRepository<PeriodicPaymentEntity, UUID> {

    Flux<PeriodicPaymentEntity> findByBasketId(UUID basketId);
}
