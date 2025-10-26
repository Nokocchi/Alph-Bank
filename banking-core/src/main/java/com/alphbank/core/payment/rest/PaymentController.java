package com.alphbank.core.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.rest.model.CreatePaymentRequest;
import com.alphbank.core.payment.rest.model.Payment;
import com.alphbank.core.payment.rest.model.PaymentSearchRestResponse;
import com.alphbank.core.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Description")
public class PaymentController {

    private final PaymentService paymentService;
    private final JsonLog jsonLog;

    // TODO: Should probably just take an accountId or customerId and use accountService to find recipientIban to avoid wrong use of this endpoint
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<PaymentSearchRestResponse>> searchPayments(
            @RequestParam(name = "from-account-id", required = true) UUID fromAccountId,
            @RequestParam(name = "recipient-iban", required = true) String recipientIban) {
        log.info("Searching payments with fromAccountId {} and recipientIban {}", fromAccountId, recipientIban);
        return paymentService.findAllPaymentsOptionalFilters(fromAccountId, recipientIban)
                .collectList()
                .map(PaymentSearchRestResponse::new)
                .doOnNext(response -> log.info("Returning payments {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching payments", e))
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        log.info("Creating payment with request body {}", jsonLog.format(createPaymentRequest));
        return paymentService.createPayment(createPaymentRequest)
                .doOnNext(response -> log.info("Returning newly created payment {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e))
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deletePayment(@PathVariable("id") UUID paymentId) {
        log.info("Deleting payment with paymentId {}", paymentId);
        return paymentService.deletePayment(paymentId)
                .doOnSuccess(response -> log.info("Deleted payment with id {}", paymentId))
                .doOnError(e -> log.error("Error deleting payment with id " + paymentId, e))
                .then(Mono.just(toResponseEntity(paymentId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Payment>> getPayment(@PathVariable("id") UUID paymentId) {
        log.info("Getting payment with paymentId {}", paymentId);
        return paymentService
                .getPayment(paymentId)
                .doOnNext(response -> log.info("Getting payment with id {}", paymentId))
                .doOnError(e -> log.error("Error getting payment with id " + paymentId, e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
