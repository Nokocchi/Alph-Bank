package com.alphbank.corecustomerservice.service.repository;

import com.alphbank.corecustomerservice.service.repository.model.CustomerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerRepository extends R2dbcRepository<CustomerEntity, UUID> {

    Flux<CustomerEntity> findAllByGovernmentId(String governmentId);

}
