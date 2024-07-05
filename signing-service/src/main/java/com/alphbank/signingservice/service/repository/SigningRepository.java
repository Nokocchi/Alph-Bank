package com.alphbank.signingservice.service.repository;

import com.alphbank.signingservice.service.repository.model.SigningSessionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SigningRepository extends R2dbcRepository<SigningSessionEntity, UUID> {

}
