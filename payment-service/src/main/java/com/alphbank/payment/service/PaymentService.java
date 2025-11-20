package com.alphbank.payment.service;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.client.CorePaymentClient;
import com.alphbank.payment.rest.error.model.BasketCreationException;
import com.alphbank.payment.rest.error.model.BasketNotFoundException;
import com.alphbank.payment.rest.error.model.CannotDeleteBasketWithAuthorizationException;
import com.alphbank.payment.rest.error.model.PaymentNotFoundException;
import com.alphbank.payment.service.amqp.configuration.RabbitConfigurationProperties;
import com.alphbank.payment.service.client.signingservice.SigningServiceClient;
import com.alphbank.payment.service.client.signingservice.configuration.SigningServiceClientConfigurationProperties;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionRequest;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionResponse;
import com.alphbank.payment.service.config.PaymentServiceConfigurationProperties;
import com.alphbank.payment.service.model.*;
import com.alphbank.payment.service.repository.BasketRepository;
import com.alphbank.payment.service.repository.PaymentRepository;
import com.alphbank.payment.service.repository.PeriodicPaymentRepository;
import com.alphbank.payment.service.repository.model.PaymentEntity;
import com.alphbank.payment.service.repository.model.PeriodicPaymentEntity;
import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PeriodicPaymentRepository periodicPaymentRepository;
    private final BasketRepository basketRepository;
    private final SigningServiceClient signingServiceClient;
    private final CorePaymentClient corePaymentClient;
    private final JsonLog jsonLog;

    // TODO: Get rid of these
    private final SigningServiceClientConfigurationProperties signingServiceProperties;
    private final RabbitConfigurationProperties rabbitProperties;
    private final PaymentServiceConfigurationProperties paymentServiceProperties;


    public Mono<Payment> createPayment(Payment payment) {
        log.info("Creating payment: {}", jsonLog.format(payment));
        return Mono.just(payment)
                .map(PaymentEntity::from)
                .flatMap(paymentRepository::save)
                .map(PaymentEntity::toModel);
    }

    public Mono<Payment> findPaymentById(UUID paymentId) {
        log.info("Searching for payment with id: {}", paymentId);
        return Mono.just(paymentId)
                .flatMap(paymentRepository::findById)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .doOnNext(payment -> log.info("Found payment with id: {}", paymentId))
                .map(PaymentEntity::toModel)
                .flatMap(this::appendTransactionStatus);

    }

    public Mono<Void> deletePayment(UUID paymentId) {
        log.info("Deleting payment with id: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .flatMap(this::getIdIfDeletable)
                .flatMap(paymentRepository::deleteById)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .then();
    }

    private Mono<UUID> getIdIfDeletable(PaymentEntity payment) {
        return canDeletePayment(payment)
                .flatMap(canDelete -> canDelete ? Mono.just(payment.getId()) : Mono.empty());
    }

    // TODO: Not sure if this works
    // Try to fetch the basket's editable flag and return that.
    // If there is no basketId, or if a basket could not be found, we have an empty Mono, and we default to true.
    private Mono<Boolean> canDeletePayment(PaymentEntity paymentEntity) {
        return Mono.justOrEmpty(paymentEntity.getBasketId())
                .flatMap(basketRepository::findById)
                .map(SigningBasketEntity::getSigningStatus)
                .map(BasketSigningStatus::editable)
                .defaultIfEmpty(true);
    }

    private Mono<Payment> appendTransactionStatus(Payment payment) {
        if (payment.basketId() == null) {
            return Mono.just(payment);
        }

        return basketRepository.findById(payment.basketId())
                .map(SigningBasketEntity::getSigningStatus)
                .flatMap(signingStatus -> getPaymentTransactionStatus(payment, signingStatus))
                .map(payment::withTransactionStatus)
                .thenReturn(payment);
    }

    private Mono<PeriodicPayment> appendTransactionStatus(PeriodicPayment payment) {
        if (payment.basketId() == null) {
            return Mono.just(payment);
        }

        return basketRepository.findById(payment.basketId())
                .map(SigningBasketEntity::getSigningStatus)
                .flatMap(signingStatus -> getPaymentTransactionStatus(payment, signingStatus))
                .map(payment::withTransactionStatus)
                .thenReturn(payment);
    }

    private Mono<PaymentTransactionStatus> getPaymentTransactionStatus(Payment payment, BasketSigningStatus signingStatus) {
        return switch (signingStatus) {
            case NOT_YET_STARTED -> Mono.just(PaymentTransactionStatus.RECEIVED);
            case SIGNING_SESSION_CREATED -> Mono.just(PaymentTransactionStatus.AUTHORIZATION_CREATED);
            case SIGNING_IN_PROGRESS -> Mono.just(PaymentTransactionStatus.AUTHORIZATION_STARTED);
            case FAILED -> Mono.just(PaymentTransactionStatus.AUTHORIZATION_FAILED);
            // TODO: Use @Transactional to ensure that a payment is only set to COMPLETED when it has a coreReference
            //case COMPLETED -> corePaymentClient.getPayment(payment.coreReference())
            //        .map(PaymentDTO::getStatus)
            //        .map(PaymentTransactionStatus::fromCoreTransactionStatus);
            case COMPLETED -> Mono.just(PaymentTransactionStatus.CORE_PAYMENT_FAILED); // TODO: Delete
        };
    }

    private Mono<PeriodicPaymentTransactionStatus> getPaymentTransactionStatus(PeriodicPayment payment, BasketSigningStatus signingStatus) {
        return switch (signingStatus) {
            case NOT_YET_STARTED -> Mono.just(PeriodicPaymentTransactionStatus.RECEIVED);
            case SIGNING_SESSION_CREATED -> Mono.just(PeriodicPaymentTransactionStatus.AUTHORIZATION_CREATED);
            case SIGNING_IN_PROGRESS -> Mono.just(PeriodicPaymentTransactionStatus.AUTHORIZATION_STARTED);
            case FAILED -> Mono.just(PeriodicPaymentTransactionStatus.AUTHORIZATION_FAILED);
            case COMPLETED -> Mono.just(PeriodicPaymentTransactionStatus.CORE_ACCEPTED);
        };
    }


    public Mono<PeriodicPayment> createPeriodicPayment(PeriodicPayment periodicPayment) {
        log.info("Creating periodic payment: {}", jsonLog.format(periodicPayment));
        return Mono.just(periodicPayment)
                .map(PeriodicPaymentEntity::from)
                .flatMap(periodicPaymentRepository::save)
                .map(PeriodicPaymentEntity::toModel);
    }

    public Mono<PeriodicPayment> findPeriodicPaymentById(UUID paymentId) {
        log.info("Searching for periodic payment with id: {}", paymentId);
        return Mono.just(paymentId)
                .flatMap(periodicPaymentRepository::findById)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .doOnNext(payment -> log.info("Found periodic payment with id: {}", paymentId))
                .map(PeriodicPaymentEntity::toModel)
                .flatMap(this::appendTransactionStatus);

    }

    @Transactional
    public Mono<SigningBasket> createBasket(SigningBasket signingBasket, List<String> paymentIds) {
        log.info("Creating signing basket: {}", signingBasket);

        List<UUID> paymentUUIDs = paymentIds.stream().map(UUID::fromString).toList();

        Flux<PaymentEntity> payments = paymentRepository.findAllById(paymentUUIDs).cache();
        Flux<PeriodicPaymentEntity> periodicPayments = periodicPaymentRepository.findAllById(paymentUUIDs).cache();

        Mono<Void> validatePaymentsCount = Flux.merge(payments, periodicPayments)
                .count()
                .flatMap(count -> count == paymentUUIDs.size() ? Mono.empty() : Mono.error(new BasketCreationException(paymentUUIDs.size(), count)));

        return validatePaymentsCount.then(
                Mono.just(signingBasket)
                        .map(SigningBasketEntity::from)
                        .flatMap(basketRepository::save)
                        .flatMap(basket -> setBasketIdOnPayments(basket, payments, periodicPayments))
                        .flatMap(savedBasket ->
                                Mono.zip(
                                        Mono.just(savedBasket),
                                        payments.collectList(),
                                        periodicPayments.collectList()
                                )
                        )
                        .map(TupleUtils.function(SigningBasket::from)));
    }

    private Mono<SigningBasketEntity> setBasketIdOnPayments(SigningBasketEntity basket, Flux<PaymentEntity> payments, Flux<PeriodicPaymentEntity> periodicPayments) {
        Mono<Integer> updatePayments = payments
                .map(PaymentEntity::getId)
                .collectList()
                .flatMap(paymentIds -> paymentIds.isEmpty() ? Mono.empty() : paymentRepository.updateBasketIdForPayments(basket.getId(), paymentIds));

        Mono<Integer> updatePeriodicPayments = periodicPayments
                .map(PeriodicPaymentEntity::getId)
                .collectList()
                .flatMap(paymentIds -> paymentIds.isEmpty() ? Mono.empty() : periodicPaymentRepository.updateBasketIdForPayments(basket.getId(), paymentIds));

        return Mono.when(updatePayments, updatePeriodicPayments)
                .thenReturn(basket);
    }

    public Mono<SigningBasketLinks> getSigningBasketLinks(UUID basketId) {
        SigningBasketLinks links = SigningBasketLinks.builder()
                .basketStatusURI(URI.create(paymentServiceProperties.getBasketStatusURITemplate().formatted(basketId)))
                .basketAuthorizationURI(URI.create(paymentServiceProperties.getBasketAuthorizationURITemplate().formatted(basketId)))
                .basketAuthorizationStatsURI(URI.create(paymentServiceProperties.getBasketAuthorizationStatusURITemplate().formatted(basketId, basketId)))
                .build();
        return Mono.just(links);
    }

    public Mono<Void> deleteBasket(UUID basketId) {
        return basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new BasketNotFoundException(basketId)))
                .filter(basket -> basket.getSigningStatus() == BasketSigningStatus.NOT_YET_STARTED)
                .switchIfEmpty(Mono.error(new CannotDeleteBasketWithAuthorizationException(basketId)))
                .then(Mono.defer(() -> paymentRepository.clearBasketIdOfPaymentsWithBasketId(basketId)))
                .then(Mono.defer(() -> periodicPaymentRepository.clearBasketIdOfPaymentsWithBasketId(basketId)))
                .then(Mono.defer(() -> basketRepository.deleteById(basketId)));
    }

    public Mono<SigningBasket> getSigningBasket(UUID basketId) {
        return basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new BasketNotFoundException(basketId)))
                .flatMap(basketEntity -> Mono.zip(
                        Mono.just(basketEntity),
                        paymentRepository.findByBasketId(basketId).collectList(),
                        periodicPaymentRepository.findByBasketId(basketId).collectList()
                ))
                .map(TupleUtils.function(SigningBasket::from));
    }

    public Mono<BasketSigningStatus> getSigningBasketAuthorizationStatus(UUID basketId) {
        return basketRepository.findById(basketId)
                .switchIfEmpty(Mono.error(new BasketNotFoundException(basketId)))
                .map(SigningBasketEntity::getSigningStatus);
    }

    public Mono<SigningBasket> setupSigningSessionForBasket(UUID basketId, String languageCode, String nationalId) {
        Mono<SigningBasketEntity> findBasketEntity = basketRepository.findById(basketId).cache();

        return findBasketEntity
                .zipWith(getSigningBasketDocument(basketId))
                .map(TupleUtils.function((basketEntity, formattedDocument) -> createSetupSigningSessionRequest(formattedDocument, languageCode, nationalId, basketEntity.getOnSignSuccessRedirectUri(), basketEntity.getOnSignFailedRedirectUri())))
                .flatMap(signingServiceClient::setupSigningSession)
                .zipWith(findBasketEntity)
                .flatMap(TupleUtils.function(this::persistSigningSessionData))
                .then(Mono.defer(() -> getSigningBasket(basketId)));
    }

    private Mono<String> getSigningBasketDocument(UUID basketId) {
        String singlePaymentDocumentTemplate = signingServiceProperties.getSinglePaymentDocumentToSignTemplate();
        String periodicPaymentDocumentTemplate = signingServiceProperties.getGetPeriodicPaymentDocumentToSignTemplate();

        Flux<String> singlePaymentDocuments = paymentRepository.findByBasketId(basketId)
                .map(paymentEntity -> paymentEntity.asFormattedDocument(singlePaymentDocumentTemplate));

        Flux<String> periodicPaymentDocuments = periodicPaymentRepository.findByBasketId(basketId)
                .map(periodicPaymentEntity -> periodicPaymentEntity.asFormattedDocument(periodicPaymentDocumentTemplate));

        return Flux.concat(singlePaymentDocuments, periodicPaymentDocuments)
                .collectList()
                .map(formattedDocumentList -> String.join("\n", formattedDocumentList));
    }

    private Mono<SigningBasketEntity> persistSigningSessionData(SetupSigningSessionResponse signingSession, SigningBasketEntity basketEntity) {
        SigningBasketEntity updatedBasketEntity = basketEntity
                .withSigningSessionId(signingSession.signingSessionId())
                .withSigningURI(signingSession.signingUrl())
                .withSigningStatus(BasketSigningStatus.SIGNING_SESSION_CREATED);
        return basketRepository.save(updatedBasketEntity);
    }

    //---------------------------

    private SetupSigningSessionRequest createSetupSigningSessionRequest(String formattedDocument, String nationalId, String languageCode, URI onSuccessRedirectURI, URI onFailedRedirectURI) {
        return new SetupSigningSessionRequest(
                UUID.randomUUID(),
                nationalId,
                Locale.of("sv", "SE"),
                rabbitProperties.getPaymentSigningStatusRoutingKey(),
                formattedDocument,
                onSuccessRedirectURI.toString(),
                onFailedRedirectURI.toString()
        );
    }

    /*
    private Mono<SigningBasketEntity> findEditableBasket(UUID customerId) {
        return basketRepository.findByCustomerId(customerId)
                .filter(this::isEditable);
    }


    private InternalPaymentDTO convertPaymentToRestModel(PaymentEntity paymentEntity) {
        return new InternalPaymentDTO(paymentEntity.getId(),
                paymentEntity.getAccountId(),
                paymentEntity.getBasketId(),
                paymentEntity.getRecipientIban(),
                paymentEntity.getMessageToSelf(),
                paymentEntity.getMessageToRecipient(),
                Money.of(paymentEntity.getAmount(), paymentEntity.getCurrency()),
                paymentEntity.getScheduledDateTime());
    }

    private BasketDTO convertBasketToRestModel(UUID basketId, List<InternalPaymentDTO> payments) {
        return new BasketDTO(basketId, payments);
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
    private Mono<Void> executeBasket(SigningBasketEntity basketEntity) {
        return paymentRepository.findByBasketId(basketEntity.getId())
                .map(paymentEntity -> toCreateCorePaymentRequest(basketEntity, paymentEntity))
                .flatMap(corePaymentClient::createPayment)
                .then();
    }

    private com.alphbank.core.client.model.CreatePaymentRequestDTO toCreateCorePaymentRequest(SigningBasketEntity basketEntity, PaymentEntity paymentEntity) {
        return com.alphbank.core.client.model.CreatePaymentRequestDTO.builder()
                .fromCustomerId(basketEntity.getCustomerId())
                .fromAccountId(paymentEntity.getAccountId())
                .recipientIban(paymentEntity.getRecipientIban())
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

    public Mono<BasketDTO> findActiveBasketByCustomerId(UUID customerId) {
        return basketRepository.findByCustomerId(customerId)
                .filter(basket -> !BasketSigningStatus.COMPLETED.toString().equals(basket.getSigningStatus()))
                .map(SigningBasketEntity::getId)
                .zipWhen(this::findPaymentsByBasketId)
                .map(TupleUtils.function(this::convertBasketToRestModel));
    }

    private Mono<List<InternalPaymentDTO>> findPaymentsByBasketId(UUID basketId) {
        return paymentRepository.findByBasketId(basketId)
                .map(this::convertPaymentToRestModel)
                .collectList();
    }

    private SetupSigningSessionRequest createSetupSigningSessionRequest(String formattedDocument, String nationalId, String languageCode, URI onSuccessRedirectURI, URI onFailedRedirectURI) {
        return new SetupSigningSessionRequest(
                nationalId,
                languageCode,
                rabbitProperties.getPaymentSigningStatusRoutingKey(),
                formattedDocument,
                onSuccessRedirectURI,
                onFailedRedirectURI
        );
    }
  */
}
