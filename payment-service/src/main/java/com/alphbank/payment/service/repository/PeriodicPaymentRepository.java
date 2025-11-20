package com.alphbank.payment.service.repository;

import com.alphbank.payment.service.repository.model.PeriodicPaymentEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface PeriodicPaymentRepository extends R2dbcRepository<PeriodicPaymentEntity, UUID> {

    Flux<PeriodicPaymentEntity> findByBasketId(UUID basketId);

    @Modifying
    @Query("UPDATE periodic_payment SET basket_id = NULL WHERE payment.basket_id = :basketId")
    Mono<Void> clearBasketIdOfPaymentsWithBasketId(UUID basketId);

    @Modifying
    @Query("UPDATE periodic_payment SET basket_id = :basketId WHERE id IN (:ids)")
    Mono<Integer> updateBasketIdForPayments(UUID basketId, List<UUID> ids);
}
