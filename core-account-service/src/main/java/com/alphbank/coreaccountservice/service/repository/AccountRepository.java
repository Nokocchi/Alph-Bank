package com.alphbank.coreaccountservice.service.repository;

import com.alphbank.coreaccountservice.service.repository.model.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends R2dbcRepository<AccountEntity, UUID> {

    Mono<AccountEntity> findByIban(String recipientIban);

    Flux<AccountEntity> findAllByCustomerId(String customerId);
}
