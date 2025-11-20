package com.alphbank.payment.service.repository;

import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BasketRepository extends R2dbcRepository<SigningBasketEntity, UUID> {

    Mono<SigningBasketEntity> findBySigningSessionId(UUID signingSessionId);
}
