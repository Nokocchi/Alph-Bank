package com.alphbank.coreloanservice.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.coreloanservice.rest.model.Basket;
import com.alphbank.coreloanservice.rest.model.Payment;
import com.alphbank.coreloanservice.rest.model.SetupSigningSessionRestRequest;
import com.alphbank.coreloanservice.rest.model.SetupSigningSessionRestResponse;
import com.alphbank.coreloanservice.service.PaymentService;
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
@RequestMapping("/basket")
public class BasketController {

    private final PaymentService paymentBasketService;
    private final JsonLog jsonLog;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Basket>> findBasketByCustomerId(@RequestParam(required = true) UUID customerId){
        log.info("Searching payment basket by fromCustomerId {}", customerId);
        return paymentBasketService.findBasketByCustomerId(customerId)
                .doOnNext(response -> log.info("Returning basket {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching for basket with fromCustomerId " + customerId, e))
                .map(this::toResponseEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteBasket(@PathVariable("id") UUID basketId){
        log.info("Deleting basket with id {}", basketId);
        return paymentBasketService.deleteBasket(basketId)
                .doOnSuccess(response -> log.info("Deleted basket with id {}", basketId))
                .doOnError(e -> log.error("Error deleting basket with id " + basketId, e))
                .then(Mono.just(toResponseEntity(basketId)));
    }

    @PostMapping("/{basketId}/authorize")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<SetupSigningSessionRestResponse>> setupSigningSessionForBasket(
            @PathVariable("basketId") UUID basketId,
            @RequestBody SetupSigningSessionRestRequest request){
        log.info("Setting up signing session {} for basket with id {}", jsonLog.format(request), basketId);
        return paymentBasketService.setupSigningSessionForBasket(basketId, request)
                .doOnNext(response -> log.info("Returning signing url {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error setting up signing session for basket with basketId " + basketId, e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
