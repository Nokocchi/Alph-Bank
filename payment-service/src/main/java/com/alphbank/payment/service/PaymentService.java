package com.alphbank.payment.service;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.client.CorePaymentClient;
import com.alphbank.core.client.model.MonetaryAmountDTO;
import com.alphbank.core.client.model.PaymentDTO;
import com.alphbank.payment.rest.error.model.PaymentNotFoundException;
import com.alphbank.payment.rest.model.response.BasketDTO;
import com.alphbank.payment.rest.model.response.InternalPaymentDTO;
import com.alphbank.payment.rest.model.request.SetupSigningSessionRestRequest;
import com.alphbank.payment.rest.model.response.SetupSigningSessionRestResponse;
import com.alphbank.payment.service.amqp.configuration.RabbitConfigurationProperties;
import com.alphbank.payment.service.amqp.model.SigningStatus;
import com.alphbank.payment.service.client.signingservice.SigningServiceClient;
import com.alphbank.payment.service.client.signingservice.configuration.SigningServiceClientConfigurationProperties;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionRequest;
import com.alphbank.payment.service.client.signingservice.model.SetupSigningSessionResponse;
import com.alphbank.payment.service.model.BasketSigningStatus;
import com.alphbank.payment.service.model.Payment;
import com.alphbank.payment.service.model.PaymentTransactionStatus;
import com.alphbank.payment.service.repository.BasketRepository;
import com.alphbank.payment.service.repository.PaymentRepository;
import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import com.alphbank.payment.service.repository.model.PaymentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BasketRepository basketRepository;
    private final SigningServiceClient signingServiceClient;
    private final CorePaymentClient corePaymentClient;
    private final JsonLog jsonLog;

    // TODO: Get rid of these
    private final SigningServiceClientConfigurationProperties signingServiceProperties;
    private final RabbitConfigurationProperties rabbitProperties;


    public Mono<Payment> createPayment(Payment payment) {
        log.info("Creating payment {}", jsonLog.format(payment));
        return Mono.just(payment)
                .map(PaymentEntity::from)
                .flatMap(paymentRepository::save)
                .map(PaymentEntity::toModel);
    }

    public Mono<Payment> findPaymentById(UUID paymentId){
        log.info("Searching for payment with id {}", paymentId);
        return Mono.just(paymentId)
                .flatMap(paymentRepository::findById)
                .doOnNext(payment -> log.info("Found payment with id {}", paymentId))
                .map(PaymentEntity::toModel)
                .flatMap(this::appendTransactionStatus)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)));
    }

    public Mono<Void> deletePayment(UUID paymentId) {
        log.info("Deleting payment with id {}", paymentId);
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .flatMap(this::getIdIfDeletable)
                .flatMap(paymentRepository::deleteById)
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(paymentId)))
                .then();
    }

    private Mono<UUID> getIdIfDeletable(PaymentEntity payment){
        return canDeletePayment(payment)
                .flatMap(canDelete -> canDelete ? Mono.just(payment.getId()) : Mono.empty());
    }

    // TODO: Not sure if this works
    // Try to fetch the basket's editable flag and return that.
    // If there is no basketId, or if a basket could not be found, we have an empty Mono, and we default to true.
    private Mono<Boolean> canDeletePayment(PaymentEntity paymentEntity){
        return Mono.justOrEmpty(paymentEntity.getBasketId())
                .flatMap(basketRepository::findById)
                .map(SigningBasketEntity::getSigningStatus)
                .map(BasketSigningStatus::editable)
                .defaultIfEmpty(true);
    }

    private Mono<Payment> appendTransactionStatus(Payment payment){
        if(payment.basketId() == null){
            return Mono.empty();
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
            case COMPLETED -> corePaymentClient.getPayment(payment.coreReference())
                    .map(PaymentDTO::getStatus)
                    .map(PaymentTransactionStatus::fromCoreTransactionStatus);
        };
    }

    private Mono<SigningBasketEntity> findEditableBasket(UUID customerId) {
        return basketRepository.findByCustomerId(customerId)
                .filter(this::isEditable);
    }

    private Mono<SigningBasketEntity> createBasket(UUID customerId) {
        SigningBasketEntity basketEntity = SigningBasketEntity.from(customerId);
        return basketRepository.save(basketEntity);
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

    private Mono<SetupSigningSessionRestResponse> persistSigningSessionData(SetupSigningSessionResponse signingSession, SigningBasketEntity basketEntity) {
        SigningBasketEntity updatedBasketEntity = basketEntity.withSigningSessionId(signingSession.signingSessionId());
        return basketRepository.save(updatedBasketEntity)
                .thenReturn(new SetupSigningSessionRestResponse(signingSession.signingUrl()));
    }

    private String asFormattedDocument(PaymentEntity paymentEntity) {
        return String.format(signingServiceProperties.getSinglePaymentDocumentToSignTemplate(),
                paymentEntity.getAmount(),
                paymentEntity.getCurrency(),
                paymentEntity.getRecipientIban(),
                paymentEntity.getScheduledDateTime());
    }
}
