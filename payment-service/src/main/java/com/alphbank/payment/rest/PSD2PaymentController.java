package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.rest.validation.ValidPaymentInitiation;
import com.alphbank.payment.service.PaymentService;
import com.alphbank.payment.service.model.Payment;
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

@Tag(name = "Payment Initiation Service (PIS) - Single Payments", description = "Based on NextGenPSD2 OpenAPI spec from Berlin Group")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PSD2PaymentController {

    private final JsonLog jsonLog;
    private final PaymentService paymentService;

    @Operation(
            summary = "Creates a payment.",
            description = "The payment is created, but in order to execute it, you must create a basket and add the payments to it, and then authorize the basket."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/sepa-credit-transfers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PaymentInitiationRequestResponse201DTO> initiatePayment(
            @RequestParam("X-Request-ID") UUID xRequestID,
            @RequestParam("PSU-IP-Address") String psuIPAddress,
            @RequestBody Mono<@Valid @ValidPaymentInitiation PaymentInitiationJsonDTO> initiatePaymentRequestDTO) {
        return initiatePaymentRequestDTO
                .doOnNext(payment -> log.info("Creating PSD2 payment with requestId: {} and dto: {}", xRequestID, jsonLog.format(payment)))
                .map(dto -> Payment.fromDTO(dto, psuIPAddress, xRequestID))
                .flatMap(paymentService::createPayment)
                .map(Payment::toPSD2InitiationDTO);
    }

    // TODO: Swagger response codes
    @Operation(summary = "Get information about the payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PaymentInitiationWithStatusResponseDTO> getPaymentInformation(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get PSD2 payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.findPaymentById(paymentId)
                .map(Payment::toPSD2InfoDTO);
    }

    // TODO: Check openapi spec for which error codes to use when trying to cancel payment which has ongoing authorization
    //  add swagger annotations for response codes
    @Operation(
            summary = "Cancels a payment.",
            description = "The payment is marked as cancelled and will not be executed, even if its basket is authorized. The payment cannot be cancelled if there is an ongoing basket authorization."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/sepa-credit-transfers/{payment-id}")
    public Mono<PaymentInitiationCancelResponse202DTO> cancelPayment(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Cancelling PSD2 payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.deletePayment(paymentId)
                .thenReturn(PaymentInitiationCancelResponse202DTO.builder()
                        .transactionStatus(TransactionStatusDTO.RCVD) // If the deletion went well, no authorization had been started. (Should probably use Payment.toPSD2TransactionStatus()..)
                        .build());
    }

    @Operation(summary = "Get the status of the payment.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/sepa-credit-transfers/{payment-id}/status")
    public Mono<PaymentInitiationStatusResponse200JsonDTO> getPaymentInitiationStatus(
            @RequestParam("paymentId") @Valid UUID paymentId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get transaction status for PSD2 payment with id: {} and requestId: {}", paymentId, xRequestID);
        return paymentService.findPaymentById(paymentId)
                .map(Payment::toPSD2StatusDTO);
    }


}
