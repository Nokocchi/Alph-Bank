package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.rest.validation.ValidPeriodicPaymentInitiation;
import com.alphbank.payment.service.PaymentService;
import com.alphbank.payment.service.model.PeriodicPayment;
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
    private final PaymentService paymentService;

    @Operation(
            summary = "Creates a periodic payment.",
            description = "The periodic payment is created, but in order to execute it, you must create a basket and add the periodic payments to it, and then authorize the basket."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/sepa-credit-transfers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PaymentInitiationRequestResponse201DTO> initiatePeriodicPayment(
            @RequestHeader("X-Request-ID") UUID xRequestID,
            @RequestHeader("PSU-IP-Address") String psuIPAddress, //TODO: Mono<@Valid @ValidPeriodicPaymentInitiation PeriodicPaymentInitiationJsonDTO>...? Or outside of Mono? Or not at all?
            @RequestBody @Valid @ValidPeriodicPaymentInitiation PeriodicPaymentInitiationJsonDTO initiatePaymentRequestDTO) {
        return Mono.just(initiatePaymentRequestDTO)
                .doOnNext(payment -> log.info("Creating PSD2 periodic payment with requestId: {} and dto: {}", xRequestID, jsonLog.format(payment)))
                .map(dto -> PeriodicPayment.fromDTO(dto, psuIPAddress))
                .flatMap(paymentService::createPeriodicPayment)
                .map(PeriodicPayment::toPSD2InitiationDTO);
    }

    @Operation(summary = "Get information about the periodic payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PeriodicPaymentInitiationWithStatusResponseDTO> getPeriodicPaymentInformation(
            @PathVariable("payment-id") @Valid UUID paymentId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get PSD2 periodic payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.findPeriodicPaymentById(paymentId)
                .map(PeriodicPayment::toPSD2InfoDTO);
    }

    @Operation(
            summary = "Cancels a periodic payment.",
            description = "The periodic payment is marked as cancelled and will not be executed, even if its basket is authorized. The periodic payment cannot be cancelled if there is an ongoing basket authorization."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PaymentInitiationCancelResponse202DTO> cancelPeriodicPayment(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Cancelling PSD2 periodic payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.deletePayment(paymentId)
                .thenReturn(PaymentInitiationCancelResponse202DTO.builder()
                        .transactionStatus(TransactionStatusDTO.RCVD) // If the deletion went well, no authorization had been started. (Should probably use Payment.toPSD2TransactionStatus()..)
                        .build());
    }

    @Operation(summary = "Get the status of the periodic payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}/status")
    public Mono<PaymentInitiationStatusResponse200JsonDTO> getPeriodicPaymentInitiationStatus(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get transaction status for PSD2 periodic payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.findPeriodicPaymentById(paymentId)
                .map(PeriodicPayment::toPSD2StatusDTO);
    }
}
