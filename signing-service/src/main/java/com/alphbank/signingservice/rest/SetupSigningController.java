package com.alphbank.signingservice.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.signingservice.rest.model.SetupSigningSessionRequest;
import com.alphbank.signingservice.rest.model.SetupSigningSessionResponse;
import com.alphbank.signingservice.rest.model.SigningSession;
import com.alphbank.signingservice.service.SigningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

// This controller is for clients wanting to set up a signing session for a customer and monitor the status
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/signing")
public class SetupSigningController {

    private final SigningService signingService;
    private final JsonLog jsonLog;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<SetupSigningSessionResponse>> setupSigningSession(@RequestBody SetupSigningSessionRequest setupSigningSessionRequest){
        log.info("Setting up signing session {}", jsonLog.format(setupSigningSessionRequest));
        return signingService.setupSigningSession(setupSigningSessionRequest)
                .doOnNext(response -> log.info("Returning newly set up signing session {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error setting up signing session", e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
