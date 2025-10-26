package com.alphbank.core.customer.service.repository;

import com.alphbank.core.customer.service.repository.model.CustomerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerRepository extends R2dbcRepository<CustomerEntity, UUID> {

    Flux<CustomerEntity> findAllByNationalId(String nationalId);

}
