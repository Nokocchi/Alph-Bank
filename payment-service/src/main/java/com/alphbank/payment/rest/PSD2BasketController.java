package com.alphbank.payment.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.rest.validation.UUIDNotNullPaymentIds;
import com.alphbank.payment.service.PaymentService;
import com.alphbank.payment.service.model.BasketSigningStatus;
import com.alphbank.payment.service.model.SigningBasket;
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
import reactor.function.TupleUtils;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Signing Baskets Service (SBS)", description = "Based on NextGenPSD2 OpenAPI spec from Berlin Group")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1/signing-baskets")
public class PSD2BasketController {

    private final JsonLog jsonLog;
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
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID,
            @RequestHeader("PSU-IP-Address") String psuIPAddress,
            @RequestHeader("TPP-Redirect-URI") @Valid URI tppRedirectURI,
            @RequestHeader("TPP-Nok-Redirect-URI") @Valid URI tppNokRedirectURI,
            @RequestBody @Valid @UUIDNotNullPaymentIds SigningBasketDTO signingBasketDTO) {
        SigningBasket signingBasket = SigningBasket.from(psuIPAddress, tppRedirectURI, tppNokRedirectURI);
        return Mono.just(signingBasketDTO)
                .doOnNext(request -> log.info("Creating PSD2 signing basket with requestId: {} and dto: {}", xRequestID, jsonLog.format(request)))
                .flatMap(dto -> paymentService.createBasket(signingBasket, dto.getPaymentIds()))
                .zipWhen(basket -> paymentService.getSigningBasketLinks(basket.id()))
                .map(TupleUtils.function(SigningBasket::toPSD2InitiationDTO));
    }

    @Operation(
            summary = "Deletes a signing basket.",
            description = "The payments themselves are not deleted, and the signing basket cannot be deleted if any authorization has begun. Payments must be deleted individually in the appropriate payments controller."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{basketId}")
    public Mono<Void> deleteSigningBasket(
            @PathVariable @Valid UUID basketId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Delete PSD2 basket with id: {} and requestId: {}", basketId, xRequestID);
        return paymentService.deleteBasket(basketId);
    }


    @Operation(summary = "Gets a signing basket and the payments inside.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{basketId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SigningBasketResponse200DTO> getSigningBasket(
            @PathVariable @Valid UUID basketId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get PSD2 basket with id: {} and requestId: {}", basketId, xRequestID);
        return paymentService.getSigningBasket(basketId)
                .map(SigningBasket::toPSD2InfoDTO);
    }

    @Operation(summary = "Gets the signing (authorization) status of the signing basket.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{basketId}/authorisations/{authorisationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ScaStatusResponseDTO> getSigningBasketScaStatus(
            @PathVariable @Valid UUID basketId,
            @PathVariable String authorisationId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID) {
        log.info("Get PSD2 basket authorization status with basket id: {} and requestId: {}", basketId, xRequestID);
        return paymentService.getSigningBasketAuthorizationStatus(basketId)
                .map(BasketSigningStatus::toPSD2ScaStatus)
                .map(ScaStatusResponseDTO::new);
    }

    @Operation(
            summary = "Starts the signing (authorization) process for the basket.",
            description = "A URL is returned, which the customer must be redirected to and authorize themselves. When authorization is completed, the payments in the signing basket are executed."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{basketId}/authorisations", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<StartScaprocessResponseDTO> startSigningBasketAuthorisation(
            @PathVariable @Valid UUID basketId,
            @RequestHeader("X-Request-ID") @Valid UUID xRequestID,
            @RequestHeader("PSU-Accept-Language") String languageCode
    ) {
        log.info("Start authorization of PSD2 basket with id: {}, requestId: {} and language code: {}", basketId, xRequestID, languageCode);
        String nationalId = "123456789"; // Let's imagine that this endpoint takes a JWT bearer with the PSU's national ID
        return paymentService.setupSigningSessionForBasket(basketId, languageCode, nationalId)
                .zipWhen(basket -> paymentService.getSigningBasketLinks(basket.id()))
                .map(TupleUtils.function(SigningBasket::toPSD2StartAuthorizationDTO));

    }
}
