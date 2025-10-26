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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
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
    public Mono<PaymentSearchRestResponse> searchPayments(
            @RequestParam(name = "from-account-id", required = true) UUID fromAccountId,
            @RequestParam(name = "recipient-iban", required = true) String recipientIban) {
        log.info("Searching payments with fromAccountId {} and recipientIban {}", fromAccountId, recipientIban);
        return paymentService.findAllPaymentsOptionalFilters(fromAccountId, recipientIban)
                .collectList()
                .map(PaymentSearchRestResponse::new)
                .doOnNext(response -> log.info("Returning payments {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching payments", e));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Payment> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        log.info("Creating payment with request body {}", jsonLog.format(createPaymentRequest));
        return paymentService.createPayment(createPaymentRequest)
                .doOnNext(response -> log.info("Returning newly created payment {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e));
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Payment> getPayment(@PathVariable("id") UUID paymentId) {
        log.info("Getting payment with paymentId {}", paymentId);
        return paymentService
                .getPayment(paymentId)
                .doOnNext(response -> log.info("Getting payment with id {}", paymentId))
                .doOnError(e -> log.error("Error getting payment with id " + paymentId, e));
    }

}
