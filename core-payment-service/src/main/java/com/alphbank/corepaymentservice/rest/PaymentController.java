package com.alphbank.corepaymentservice.rest;

import com.alphbank.corepaymentservice.rest.model.*;
import com.alphbank.corepaymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    // TODO: Should probably just take an accountId or customerId and use accountService to find recipientIban to avoid wrong use of this endpoint
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<PaymentSearchRestResponse>> searchPayments(
            @RequestParam(required = true) UUID fromAccountId,
            @RequestParam(required = true) String recipientIban){
        System.out.println("Received search request!");
        return paymentService.findAllPaymentsOptionalFilters(fromAccountId, recipientIban)
                .collectList()
                .map(PaymentSearchRestResponse::new)
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest){
        return paymentService.createPayment(createPaymentRequest)
                .doOnNext(p -> System.out.println("PaymentId!!!: " + p.getPaymentId()))
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deletePayment(@PathVariable("id") UUID paymentId){
        return paymentService.deletePayment(paymentId)
                .then(Mono.just(toResponseEntity(paymentId)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Payment>> getPayment(@PathVariable("id") UUID paymentId){
        System.out.println("Received get request!");
        return paymentService
                .getPayment(paymentId)
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
