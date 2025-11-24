package com.alphbank.core.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.rest.model.Payment;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import com.alphbank.core.rest.model.PaymentSearchRestResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PaymentsApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        return createPaymentRequestDTO.doOnNext(request -> {
                    log.info("Creating payment with request body: {}", jsonLog.format(request));
                })
                .flatMap(paymentService::createPayment)
                .map(payment -> ResponseEntity.status(HttpStatus.CREATED).body(payment))
                .doOnNext(response -> log.info("Returning newly created payment: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e));
    }

    // TODO: Should probably just take an accountId or customerId and use accountService to find recipientIban to avoid wrong use of this endpoint
    // TODO: Filter out duplicates..
    // TODO: Server side sorting by execution data?
    @Override
    public Mono<ResponseEntity<Flux<PaymentDTO>>> searchPayments(UUID fromAccountId, String recipientIban, ServerWebExchange exchange) {
        log.info("Searching payments with fromAccountId: {} and recipientIban: {}", fromAccountId, recipientIban);
        Flux<PaymentDTO> payments = paymentService.findAllPaymentsOptionalFilters(fromAccountId, recipientIban)
                .map(Payment::toDTO)
                .doOnNext(response -> log.info("Returning payments: {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching payments", e));

        return Mono.just(ResponseEntity.ok(payments));
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
    public Mono<ResponseEntity<PaymentDTO>> getPayment(UUID paymentId, ServerWebExchange exchange) {
        log.info("Getting payment with paymentId: {}", paymentId);
        return paymentService
                .getPayment(paymentId)
                .doOnNext(payment -> log.info("Retrieved payment: {}", jsonLog.format(payment)))
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error getting payment with id: {}", paymentId, e));
    }

}
