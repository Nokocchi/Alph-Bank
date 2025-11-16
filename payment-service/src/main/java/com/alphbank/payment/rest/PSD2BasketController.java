package com.alphbank.payment.rest;

import com.alphbank.payment.service.PaymentService;
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

import java.net.URI;
import java.util.UUID;

@Tag(name = "Signing Baskets Service (SBS", description = "Based on NextGenPSD2 OpenAPI spec from Berlin Group")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/signing-baskets")
public class PSD2BasketController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Creates a signing basket.",
            description = """
                    A payment can be executed by authorizing a signing basket that holds the payment.
                    If you create a new signing basket with paymentIds that already exist in another basket, the payments will be moved to the new basket.
                    Once an authorization has begun, payments can no longer be cancelled or moved to another basket.
                    """
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SigningBasketResponse201DTO> createSigningBasket(
            @RequestParam("X-Request-ID") @Valid UUID xRequestID,
            @RequestParam("PSU-IP-Address") String psUIPAddress,
            @RequestParam("TPP-Redirect-URI") @Valid URI tpPRedirectURI,
            @RequestParam("TPP-Nok-Redirect-URI") @Valid URI tpPNokRedirectURI,
            @RequestBody Mono<@Valid SigningBasketDTO> signingBasketDTO) {
        return Mono.empty();

    }

    @Operation(
            summary = "Deletes a signing basket.",
            description = "The payments themselves are not deleted, and the signing basket cannot be deleted if any authorization has begun. Payments must be deleted individually in the appropriate payments controller."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{basketId}")
    public Mono<Void> deleteSigningBasket(
            @PathVariable @Valid UUID basketId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }


    @Operation(summary = "Gets a signing basket and the payments inside.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{basketId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SigningBasketResponse200DTO> getSigningBasket(
            @PathVariable @Valid UUID basketId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }

    @Operation(summary = "Gets the signing (authorization) status of the signing basket.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{basketId}/authorisations/{authorisationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ScaStatusResponseDTO> getSigningBasketScaStatus(
            @PathVariable @Valid UUID basketId,
            @PathVariable String authorisationId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }

    @Operation(
            summary = "Starts the signing (authorization) process for the basket.",
            description = "A URL is returned, which the customer must be redirected to and authorize themselves. When authorization is completed, the payments in the signing basket are executed."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{basketId}/authorisations", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<StartScaprocessResponseDTO> startSigningBasketAuthorisation(
            @PathVariable @Valid UUID basketId,
            @RequestParam("X-Request-ID") @Valid UUID xRequestID) {
        return Mono.empty();
    }
}
