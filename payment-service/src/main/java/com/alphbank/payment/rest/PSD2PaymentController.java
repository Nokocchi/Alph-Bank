package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.berlingroup.rest.model.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PSD2PaymentController {

    private final JsonLog jsonLog;

    @PostMapping("/{payment-product}")
    public Mono<PaymentInitiationRequestResponse201DTO> initiatePayment(@PathVariable("payment-product") String paymentProduct, UUID xRequestID, String psUIPAddress, Mono<PaymentInitiationJsonDTO> initiatePaymentRequestDTO) {
        return Mono.empty();
    }

    @DeleteMapping("/{payment-product}/{payment-id}")
    public Mono<PaymentInitiationCancelResponse202DTO> cancelPayment(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{payment-product}/{payment-id}")
    public Mono<GetPaymentInformation200ResponseDTO> getPaymentInformation(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{payment-product}/{payment-id}/status")
    public Mono<PaymentInitiationStatusResponse200JsonDTO> getPaymentInitiationStatus(String paymentProduct, String paymentId, UUID xRequestID) {
        return Mono.empty();
    }


}
