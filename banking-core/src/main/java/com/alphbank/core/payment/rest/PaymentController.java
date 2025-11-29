package com.alphbank.core.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PaymentsApi;
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
public class PaymentController implements PaymentsApi {

    private final PaymentService paymentService;
    private final JsonLog jsonLog;

    @Override
    public Mono<ResponseEntity<PaymentDTO>> createPayment(Mono<CreatePaymentRequestDTO> createPaymentRequestDTO, ServerWebExchange exchange) {
        return createPaymentRequestDTO
                .doOnNext(request -> log.info("Creating payment with request body: {}", jsonLog.format(request)))
                .map(Payment::from)
                .flatMap(paymentService::createPayment)
                .map(Payment::toDTO)
                .doOnNext(response -> log.info("Returning newly created payment: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e))
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment));
    }

    @Override
    public Mono<ResponseEntity<PaymentDTO>> getPayment(UUID paymentId, ServerWebExchange exchange) {
        log.info("Getting payment with paymentId: {}", paymentId);
        return paymentService
                .getPayment(paymentId)
                .map(Payment::toDTO)
                .doOnNext(payment -> log.info("Retrieved payment: {}", jsonLog.format(payment)))
                .doOnError(e -> log.error("Error getting payment with id: {}", paymentId, e))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePayment(UUID paymentId, ServerWebExchange exchange) {
        log.info("Deleting payment with paymentId: {}", paymentId);
        return paymentService.deletePayment(paymentId)
                .doOnSuccess(ignored -> log.info("Deleted payment with id: {}", paymentId))
                .doOnError(e -> log.error("Error deleting payment with id: {}", paymentId, e))
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<PaymentDTO>>> searchPayments(UUID customerId, ServerWebExchange exchange) {
        log.info("Searching pending payments with customerId: {}", customerId);
        Flux<PaymentDTO> payments = paymentService.getPendingPaymentsByCustomerId(customerId)
                .map(Payment::toDTO)
                .doOnNext(response -> log.info("Returning payments: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching payments", e));

        return Mono.just(ResponseEntity.ok(payments));
    }
}
