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

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<SearchPaymentResponse>> searchPayments(@RequestParam UUID customerId, @RequestParam UUID accountId){
        return paymentService.findAllPaymentsByCustomerIdAndAccountId(customerId, accountId)
                .collectList()
                .map(SearchPaymentResponse::new)
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest){
        return paymentService.createPayment(createPaymentRequest)
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
