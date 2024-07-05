package com.alphbank.loanapplicationservice.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.loanapplicationservice.rest.model.CreateLoanApplicationRequest;
import com.alphbank.loanapplicationservice.rest.model.CreateLoanApplicationResponse;
import com.alphbank.loanapplicationservice.rest.model.SearchLoansResponse;
import com.alphbank.loanapplicationservice.service.LoanApplicationService;
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
@RequestMapping("/loan_application")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;
    private final JsonLog jsonLog;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<SearchLoansResponse>> searchLoans(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID accountId){
        log.info("Search loan applications by customerId {}, accountId {}", customerId, accountId);
        return loanApplicationService.findAllLoanApplicationsByCustomerIdOrAccountId(customerId, accountId)
                .collectList()
                .map(SearchLoansResponse::new)
                .doOnNext(response -> log.info("Returning loan applications {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching loan applications", e))
                .map(this::toResponseEntity);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<CreateLoanApplicationResponse>> createLoanApplication(@RequestBody CreateLoanApplicationRequest createLoanApplicationRequest){
        log.info("Creating loan application {}", jsonLog.format(createLoanApplicationRequest));
        return loanApplicationService.createLoanApplication(createLoanApplicationRequest)
                .doOnNext(response -> log.info("Returning newly created loan application {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating loan application", e))
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
