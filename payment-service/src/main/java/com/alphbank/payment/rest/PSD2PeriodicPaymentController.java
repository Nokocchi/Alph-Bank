package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.berlingroup.rest.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Payment Initiation Service (PIS) - Periodic Payments", description = "Based on NextGenPSD2 OpenAPI spec from Berlin Group")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/periodic-payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PSD2PeriodicPaymentController {

    private final JsonLog jsonLog;

    @Operation(
            summary = "Creates a payment.",
            description = "The payment is created, but in order to execute it, you must create a basket and add the payments to it, and then authorize the basket."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/sepa-credit-transfers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PaymentInitiationRequestResponse201DTO> initiatePeriodicPayment(
            @RequestParam("X-Request-ID") UUID xRequestID,
            @RequestParam("PSU-IP-Address") String psUIPAddress,
            @RequestBody @Valid Mono<PeriodicPaymentInitiationJsonDTO> initiatePaymentRequestDTO) {
        return Mono.empty();
    }

    @Operation(
            summary = "Cancels a payment.",
            description = "The payment is marked as cancelled and will not be executed, even if its basket is authorized. The payment cannot be cancelled if there is an ongoing basket authorization."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PaymentInitiationCancelResponse202DTO> cancelPeriodicPayment(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }

    @Operation(summary = "Get information about the payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PeriodicPaymentInitiationWithStatusResponseDTO> getPeriodicPaymentInformation(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }

    @Operation(summary = "Get the status of the payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}/status")
    public Mono<PaymentInitiationStatusResponse200JsonDTO> getPeriodicPaymentInitiationStatus(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }


}
