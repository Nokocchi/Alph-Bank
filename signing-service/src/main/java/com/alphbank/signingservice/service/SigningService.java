package com.alphbank.signingservice.service;

import com.alphbank.signingservice.rest.model.SetupSigningSessionRequest;
import com.alphbank.signingservice.rest.model.SetupSigningSessionResponse;
import com.alphbank.signingservice.rest.model.SigningSession;
import com.alphbank.signingservice.rest.model.SigningStatus;
import com.alphbank.signingservice.service.amqp.RabbitMQService;
import com.alphbank.signingservice.service.config.SigningServiceConfigurationProperties;
import com.alphbank.signingservice.service.error.SigningSessionNotFoundException;
import com.alphbank.signingservice.service.repository.SigningRepository;
import com.alphbank.signingservice.service.repository.model.SigningSessionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SigningService {

    private final SigningRepository signingRepository;
    private final RabbitMQService rabbitMQService;
    private final SigningServiceConfigurationProperties properties;

    public Mono<SetupSigningSessionResponse> setupSigningSession(SetupSigningSessionRequest setupSigningSessionRequest) {
        SigningSessionEntity signingSessionEntity = SigningSessionEntity.from(setupSigningSessionRequest);
        return signingRepository.save(signingSessionEntity)
                .flatMap(this::publishSigningEntityOnRabbitMqAndConvertToSigningSessionSetupResponse);
    }

    private Mono<SetupSigningSessionResponse> publishSigningEntityOnRabbitMqAndConvertToSigningSessionSetupResponse(SigningSessionEntity signingSessionEntity){
        return publishSigningEntityOnRabbitMq(signingSessionEntity)
                .thenReturn(converToSetupSigningSessionResponse(signingSessionEntity.getSigningSessionId()));
    }

    private SetupSigningSessionResponse converToSetupSigningSessionResponse(UUID signingSessionId) {
        return new SetupSigningSessionResponse(signingSessionId, getSigningUrl(signingSessionId));
    }

    public Mono<SigningSession> getSigningSession(UUID signingSessionId) {
        return signingRepository.findById(signingSessionId)
                .map(this::convertToRestModel)
                .switchIfEmpty(Mono.error(new SigningSessionNotFoundException()));
    }

    private SigningSession convertToRestModel(SigningSessionEntity signingSessionEntity) {
        return SigningSession.builder()
                .signingSessionId(signingSessionEntity.getSigningSessionId())
                .customerId(signingSessionEntity.getCustomerId())
                .signingStatus(SigningStatus.valueOf(signingSessionEntity.getSigningStatus()))
                .governmentId(signingSessionEntity.getGovernmentId())
                .documentToSign(signingSessionEntity.getDocumentToSign())
                .onFailRedirectUrl(signingSessionEntity.getOnFailRedirectUrl())
                .onSuccessRedirectUrl(signingSessionEntity.getOnSuccessRedirectUrl())
                .signingUrl(getSigningUrl(signingSessionEntity.getSigningSessionId()))
                .locale(new Locale(signingSessionEntity.getLocale(), signingSessionEntity.getCountryCode()))
                .signingStatusUpdatedRoutingKey(signingSessionEntity.getSigningStatusUpdatedRoutingKey())
                .build();
    }

    private String getSigningUrl(UUID signingSessionId) {
        return String.format(properties.getSigningServiceUrlTemplate(), signingSessionId);
    }

    public Mono<Void> updateSigningSession(UUID signingSessionId, SigningStatus newStatus) {
        return signingRepository.findById(signingSessionId)
                .switchIfEmpty(Mono.error(new SigningSessionNotFoundException()))
                .map(signingEntity -> signingEntity.withSigningStatus(newStatus.name()))
                .flatMap(signingRepository::save)
                .flatMap(this::publishSigningEntityOnRabbitMq);

    }

    private Mono<Void> publishSigningEntityOnRabbitMq(SigningSessionEntity signingSessionEntity) {
        return rabbitMQService.send(convertToRestModel(signingSessionEntity));
    }
}
