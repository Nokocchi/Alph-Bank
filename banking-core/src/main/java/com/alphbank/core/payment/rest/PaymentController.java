package com.alphbank.core.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.rest.model.PaymentSearchRestResponse;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import com.alphbank.core.rest.model.PaymentSearchRestResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PaymentApi;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;
    private final JsonLog jsonLog;

    @Override
    public Mono<PaymentDTO> createPayment(Mono<CreatePaymentRequestDTO> createPaymentRequestDTO, ServerWebExchange exchange) {
        return createPaymentRequestDTO.doOnNext(request -> {
                    log.info("Creating payment with request body {}", jsonLog.format(request));
                })
                .flatMap(paymentService::createPayment)
                .doOnNext(response -> log.info("Returning newly created payment {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e));
    }

    // TODO: Should probably just take an accountId or customerId and use accountService to find recipientIban to avoid wrong use of this endpoint
    // TODO: Filter out duplicates..
    @Override
    public Mono<PaymentSearchRestResponseDTO> searchPayments(UUID fromAccountId, String recipientIban, ServerWebExchange exchange) {
        log.info("Searching payments with fromAccountId {} and recipientIban {}", fromAccountId, recipientIban);
        return paymentService.findAllPaymentsOptionalFilters(fromAccountId, recipientIban)
                .collectList()
                .map(l -> new PaymentSearchRestResponseDTO().payments(l))
                .doOnNext(response -> log.info("Returning payments {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching payments", e));
    }



    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UUID> deletePayment(@PathVariable("id") UUID paymentId) {
        log.info("Deleting payment with paymentId {}", paymentId);
        return paymentService.deletePayment(paymentId)
                .doOnSuccess(response -> log.info("Deleted payment with id {}", paymentId))
                .doOnError(e -> log.error("Error deleting payment with id " + paymentId, e))
                .thenReturn(paymentId);
    }

    @Override
    public Mono<PaymentDTO> getPayment(UUID paymentId,  ServerWebExchange exchange) {
        log.info("Getting payment with paymentId {}", paymentId);
        return paymentService
                .getPayment(paymentId)
                .doOnNext(response -> log.info("Getting payment with id {}", paymentId))
                .doOnError(e -> log.error("Error getting payment with id " + paymentId, e));
    }

}
