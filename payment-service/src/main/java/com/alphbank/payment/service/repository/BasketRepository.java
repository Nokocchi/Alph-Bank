package com.alphbank.payment.service.repository;

import com.alphbank.payment.service.repository.model.BasketEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BasketRepository extends R2dbcRepository<BasketEntity, UUID> {

    Mono<BasketEntity> findByCustomerId(UUID customerId);

    Mono<BasketEntity> findBySigningSessionId(UUID signingSessionId);
}
