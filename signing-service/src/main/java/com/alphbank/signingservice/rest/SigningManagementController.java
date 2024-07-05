package com.alphbank.signingservice.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.signingservice.rest.model.SetupSigningSessionRequest;
import com.alphbank.signingservice.rest.model.SigningSession;
import com.alphbank.signingservice.rest.model.SigningSessionStatusUpdateRequest;
import com.alphbank.signingservice.rest.model.SigningStatus;
import com.alphbank.signingservice.service.SigningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

// This controller is for the signing-flow UI to update, handle and manage ongoing signing sessions
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/signing")
public class SigningManagementController {

    private final SigningService signingService;
    private final JsonLog jsonLog;

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Boolean>> updateSigningSession(
            @PathVariable("id") UUID signingSessionId,
            @RequestBody SigningSessionStatusUpdateRequest newSigningStatusRequest){
        SigningStatus newStatus = newSigningStatusRequest.newStatus();
        log.info("Updating signing session {} with new status {}", signingSessionId, newStatus);
        return signingService.updateSigningSession(signingSessionId, newStatus)
                .doOnSuccess(response -> log.info("Succesfully updated signing session {} with new status {}", signingSessionId, newStatus))
                .doOnError(e -> log.error("Error updating signing session with new status", e))
                .thenReturn(toResponseEntity(Boolean.TRUE));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<SigningSession>> getSigningSession(@PathVariable("id") UUID signingSessionId){
        log.info("Getting signing session with id {}", signingSessionId);
        return signingService
                .getSigningSession(signingSessionId)
                .doOnNext(response -> log.info("Returning signing session {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting signing session with id " + signingSessionId, e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
