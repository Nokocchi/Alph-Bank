package com.alphbank.payment.service;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.client.CorePaymentClient;
import com.alphbank.core.client.model.MonetaryAmountDTO;
import com.alphbank.payment.rest.model.Basket;
import com.alphbank.payment.rest.model.Payment;
import com.alphbank.payment.rest.model.request.CreatePaymentRequest;
import com.alphbank.payment.rest.model.request.SetupSigningSessionRestRequest;
import com.alphbank.payment.rest.model.response.SetupSigningSessionRestResponse;
import com.alphbank.payment.service.amqp.configuration.RabbitConfigurationProperties;
import com.alphbank.payment.service.amqp.model.SigningStatus;
import com.alphbank.payment.service.client.signingservice.SigningServiceClient;
import com.alphbank.payment.service.client.signingservice.configuration.SigningServiceClientConfigurationProperties;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionRequest;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionResponse;
import com.alphbank.payment.service.model.BasketSigningStatus;
import com.alphbank.payment.service.repository.BasketRepository;
import com.alphbank.payment.service.repository.PaymentRepository;
import com.alphbank.payment.service.repository.model.BasketEntity;
import com.alphbank.payment.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BasketRepository basketRepository;
    private final SigningServiceClient signingServiceClient;
    private final SigningServiceClientConfigurationProperties signingServiceProperties;
    private final RabbitConfigurationProperties rabbitProperties;
    private final CorePaymentClient corePaymentClient;
    private final JsonLog jsonLog;

    private final Set<BasketSigningStatus> editableBasketStatuses = Set.of(BasketSigningStatus.NOT_YET_STARTED, BasketSigningStatus.FAILED);

    public Mono<Payment> createPayment(CreatePaymentRequest createPaymentRequest) {
        UUID customerId = createPaymentRequest.fromCustomerId();
        return findEditableBasket(customerId)
                .switchIfEmpty(createBasket(customerId))
                .map(basketEntity -> PaymentEntity.from(basketEntity.getBasketId(), createPaymentRequest))
                .flatMap(paymentRepository::save)
                .map(this::convertPaymentToRestModel);
    }

    private Mono<BasketEntity> findEditableBasket(UUID customerId) {
        return basketRepository.findByCustomerId(customerId)
                .filter(this::isEditable);
    }

    private boolean isEditable(BasketEntity basketEntity) {
        return editableBasketStatuses.contains(BasketSigningStatus.valueOf(basketEntity.getSigningStatus()));
    }

    private Mono<BasketEntity> createBasket(UUID customerId) {
        BasketEntity basketEntity = BasketEntity.from(customerId);
        return basketRepository.save(basketEntity);
    }

    public Mono<Object> deletePayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .flatMap(paymentEntity -> basketRepository.findById(paymentEntity.getBasketId()))
                .filter(this::isEditable)
                .map(basketEntity -> paymentRepository.deleteById(paymentId))
                .thenReturn(paymentId);
    }

    private Payment convertPaymentToRestModel(PaymentEntity paymentEntity) {
        return new Payment(paymentEntity.getPaymentId(),
                paymentEntity.getAccountId(),
                paymentEntity.getBasketId(),
                paymentEntity.getRecipientIBAN(),
                paymentEntity.getMessageToSelf(),
                paymentEntity.getMessageToRecipient(),
                Money.of(paymentEntity.getAmount(), paymentEntity.getCurrency()),
                paymentEntity.getScheduledDateTime());
    }

    private Basket convertBasketToRestModel(UUID basketId, List<Payment> payments) {
        return new Basket(basketId, payments);
    }

    public Mono<Void> handleNewSigningStatus(UUID signingSessionId, SigningStatus signingStatus) {
        BasketSigningStatus basketSigningStatus = fromSigningStatus(signingStatus);
        return basketRepository.findBySigningSessionId(signingSessionId)
                .map(entity -> entity.withSigningStatus(basketSigningStatus.toString()))
                .flatMap(basketRepository::save)
                .filter(basketEntity -> basketSigningStatus == BasketSigningStatus.COMPLETED)
                .flatMap(this::executeBasket);
    }

    // TODO: maybe have an endpoint in the core that takes a list of payments, instead of sending one payment at a time?
    private Mono<Void> executeBasket(BasketEntity basketEntity) {
        return paymentRepository.findByBasketId(basketEntity.getBasketId())
                .map(paymentEntity -> toCreateCorePaymentRequest(basketEntity, paymentEntity))
                .flatMap(corePaymentClient::createPayment)
                .then();
    }

    private com.alphbank.core.client.model.CreatePaymentRequestDTO toCreateCorePaymentRequest(BasketEntity basketEntity, PaymentEntity paymentEntity) {
        return com.alphbank.core.client.model.CreatePaymentRequestDTO.builder()
                .fromCustomerId(basketEntity.getCustomerId())
                .fromAccountId(paymentEntity.getAccountId())
                .recipientIban(paymentEntity.getRecipientIBAN())
                .messageToSelf(paymentEntity.getMessageToSelf())
                .messageToRecipient(paymentEntity.getMessageToRecipient())
                .amount(MonetaryAmountDTO.
                        builder()
                        .amount(paymentEntity.getAmount())
                        .currency(paymentEntity.getCurrency())
                        .build())
                .scheduledDateTime(paymentEntity.getScheduledDateTime())
                .build();
    }

    private BasketSigningStatus fromSigningStatus(SigningStatus signingStatus) {
        return switch (signingStatus) {
            case CREATED -> BasketSigningStatus.SIGNING_SESSION_CREATED;
            case IN_PROGRESS -> BasketSigningStatus.SIGNING_IN_PROGRESS;
            case EXPIRED, CANCELLED -> BasketSigningStatus.FAILED;
            case COMPLETED -> BasketSigningStatus.COMPLETED;
        };
    }

    public Mono<Basket> findActiveBasketByCustomerId(UUID customerId) {
        return basketRepository.findByCustomerId(customerId)
                .filter(basket -> !BasketSigningStatus.COMPLETED.toString().equals(basket.getSigningStatus()))
                .map(BasketEntity::getBasketId)
                .zipWhen(this::findPaymentsByBasketId)
                .map(TupleUtils.function(this::convertBasketToRestModel));
    }

    private Mono<List<Payment>> findPaymentsByBasketId(UUID basketId) {
        return paymentRepository.findByBasketId(basketId)
                .map(this::convertPaymentToRestModel)
                .collectList();
    }

    public Mono<UUID> deleteBasket(UUID basketId) {
        return paymentRepository.findByBasketId(basketId)
                .flatMap(paymentRepository::delete)
                .then(basketRepository.deleteById(basketId))
                .thenReturn(basketId);
    }

    public Mono<SetupSigningSessionRestResponse> setupSigningSessionForBasket(UUID basketId, SetupSigningSessionRestRequest request) {
        return paymentRepository.findByBasketId(basketId)
                .map(this::asFormattedDocument)
                .collectList()
                .map(formattedDocumentList -> String.join("\n", formattedDocumentList))
                .map(formattedDocument -> createSetupSigningSessionRequest(formattedDocument, request))
                .flatMap(signingServiceClient::setupSigningSession)
                .zipWith(basketRepository.findById(basketId))
                .flatMap(TupleUtils.function(this::persistSigningSessionData));
    }

    private SetupSigningSessionRequest createSetupSigningSessionRequest(String formattedDocument, SetupSigningSessionRestRequest request) {
        return new SetupSigningSessionRequest(
                request.customerId(),
                request.nationalId(),
                request.locale(),
                rabbitProperties.getPaymentSigningStatusRoutingKey(),
                formattedDocument,
                request.onSigningSuccessRedirectUrl(),
                request.onSigningFailedRedirectUrl()
        );
    }

    private Mono<SetupSigningSessionRestResponse> persistSigningSessionData(SetupSigningSessionResponse signingSession, BasketEntity basketEntity) {
        BasketEntity updatedBasketEntity = basketEntity.withSigningSessionId(signingSession.signingSessionId());
        return basketRepository.save(updatedBasketEntity)
                .thenReturn(new SetupSigningSessionRestResponse(signingSession.signingUrl()));
    }

    private String asFormattedDocument(PaymentEntity paymentEntity) {
        return String.format(signingServiceProperties.getSinglePaymentDocumentToSignTemplate(),
                paymentEntity.getAmount(),
                paymentEntity.getCurrency(),
                paymentEntity.getRecipientIBAN(),
                paymentEntity.getScheduledDateTime());
    }
}
