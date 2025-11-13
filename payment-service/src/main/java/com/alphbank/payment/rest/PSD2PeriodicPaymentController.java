package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.berlingroup.rest.model.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/periodic-payments")
public class PSD2PeriodicPaymentController {

    private final JsonLog jsonLog;

    @PostMapping(value = "/{payment-product}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PaymentInitiationRequestResponse201DTO> initiatePeriodicPayment(@PathVariable("payment-product") String paymentProduct, @RequestParam String xRequestID, @RequestParam String psUIPAddress, @RequestBody Mono<PeriodicPaymentInitiationJsonDTO> initiatePaymentRequestDTO) {
        return Mono.empty();
    }

    @DeleteMapping("/{payment-product}/{payment-id}")
    public Mono<PaymentInitiationCancelResponse202DTO> cancelPeriodicPayment(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{payment-product}/{payment-id}")
    public Mono<GetPaymentInformation200ResponseDTO> getPeriodicPaymentInformation(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{payment-product}/{payment-id}/status")
    public Mono<PaymentInitiationStatusResponse200JsonDTO> getPeriodicPaymentInitiationStatus(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }


}
