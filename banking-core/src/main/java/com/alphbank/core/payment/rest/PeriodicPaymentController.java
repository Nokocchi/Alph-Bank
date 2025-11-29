package com.alphbank.core.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.payment.service.model.PeriodicPayment;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.CreatePeriodicPaymentRequestDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import com.alphbank.core.rest.model.PeriodicPaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PaymentsApi;
import org.openapitools.api.PeriodicPaymentsApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class PeriodicPaymentController implements PeriodicPaymentsApi {

    private final PaymentService paymentService;
    private final JsonLog jsonLog;

    @Override
    public Mono<ResponseEntity<PeriodicPaymentDTO>> createPeriodicPayment(Mono<CreatePeriodicPaymentRequestDTO> createPeriodicPaymentRequestDTO, ServerWebExchange exchange) {
        return createPeriodicPaymentRequestDTO
                .doOnNext(request -> log.info("Creating periodic payment with request body: {}", jsonLog.format(request)))
                .map(PeriodicPayment::from)
                .flatMap(paymentService::createPeriodicPayment)
                .map(PeriodicPayment::toDTO)
                .doOnNext(response -> log.info("Returning newly created periodic payment: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating periodic payment", e))
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment));
    }

    @Override
    public Mono<ResponseEntity<PeriodicPaymentDTO>> getPeriodicPayment(UUID periodicPaymentId, ServerWebExchange exchange) {
        log.info("Getting periodic payment with periodicPaymentId: {}", periodicPaymentId);
        return paymentService
                .getPeriodicPayment(periodicPaymentId)
                .map(PeriodicPayment::toDTO)
                .doOnNext(payment -> log.info("Retrieved periodic payment: {}", jsonLog.format(payment)))
                .doOnError(e -> log.error("Error getting periodic payment with id: {}", periodicPaymentId, e))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePeriodicPayment(UUID periodicPaymentId, ServerWebExchange exchange) {
        log.info("Deleting payment with periodicPaymentId: {}", periodicPaymentId);
        return paymentService.deletePeriodicPayment(periodicPaymentId)
                .doOnSuccess(ignored -> log.info("Deleted periodic payment with id: {}", periodicPaymentId))
                .doOnError(e -> log.error("Error deleting periodic payment with id: {}", periodicPaymentId, e))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<PeriodicPaymentDTO>>> searchPeriodicPayments(UUID customerId, ServerWebExchange exchange) {
        log.info("Searching active periodic payments with customerId: {}", customerId);
        Flux<PeriodicPaymentDTO> periodicPayments = paymentService.getActivePeriodicPaymentsByCustomerId(customerId)
                .map(PeriodicPayment::toDTO)
                .doOnNext(response -> log.info("Returning periodic payments: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching periodic payments", e));

        return Mono.just(ResponseEntity.ok(periodicPayments));
    }
}
