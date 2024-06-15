package com.alphbank.corecustomerservice.service.repository;

import com.alphbank.corecustomerservice.service.repository.model.AddressEntity;
import com.alphbank.corecustomerservice.service.repository.model.CustomerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AddressRepository extends R2dbcRepository<AddressEntity, UUID> {

    Mono<AddressEntity> findByCustomerId(UUID id);
}
