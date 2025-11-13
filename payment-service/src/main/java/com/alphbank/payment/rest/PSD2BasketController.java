package com.alphbank.payment.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.berlingroup.rest.model.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/signing-baskets")
public class PSD2BasketController {

    @PostMapping
    public Mono<SigningBasketResponse201DTO> createSigningBasket(UUID xRequestID, String psUIPAddress, URI tpPRedirectURI, URI tpPNokRedirectURI, String tpPNotificationURI, Mono<SigningBasketDTO> signingBasketDTO) {
        return Mono.empty();
    }

    @DeleteMapping("/{basketId}")
    public Mono<Void> deleteSigningBasket(String basketId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{basketId}")
    public Mono<SigningBasketResponse200DTO> getSigningBasket(String basketId, UUID xRequestID) {
        return Mono.empty();
    }

    @GetMapping("/{basketId}/authorisations/{authorisationId}")
    public Mono<ScaStatusResponseDTO> getSigningBasketScaStatus(String basketId, String authorisationId, UUID xRequestID) {
        return Mono.empty();
    }

    @PostMapping("/{basketId}/authorisations")
    public Mono<StartScaprocessResponseDTO> startSigningBasketAuthorisation(String basketId, UUID xRequestID) {
        return Mono.empty();
    }
}
