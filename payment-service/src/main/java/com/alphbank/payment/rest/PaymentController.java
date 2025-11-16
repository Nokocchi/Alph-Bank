package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.rest.model.request.CreatePaymentRequest;
import com.alphbank.payment.rest.model.response.InternalPaymentDTO;
import com.alphbank.payment.service.PaymentService;
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
public class PaymentController {

    private final PaymentService paymentService;
    private final JsonLog jsonLog;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<InternalPaymentDTO>> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest){
        log.info("Creating payment {}", jsonLog.format(createPaymentRequest));
        return paymentService.createPayment(createPaymentRequest)
                .doOnNext(response -> log.info("Returning newly created payment {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating payment", e))
                .map(this::toResponseEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteLoan(@PathVariable("id") UUID paymentId){
        log.info("Deleting payment with id {}", paymentId);
        return paymentService.deletePayment(paymentId)
                .doOnSuccess(response -> log.info("Deleted payment with id {}", paymentId))
                .doOnError(e -> log.error("Error deleting payment with id " + paymentId, e))
                .then(Mono.just(toResponseEntity(paymentId)));
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
