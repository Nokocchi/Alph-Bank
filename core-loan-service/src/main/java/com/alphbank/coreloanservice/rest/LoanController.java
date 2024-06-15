package com.alphbank.coreloanservice.rest;

import com.alphbank.coreloanservice.rest.model.*;
import com.alphbank.coreloanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/loan")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<SearchLoansResponse>> searchLoans(@RequestParam UUID customerId, @RequestParam UUID accountId){
        return loanService.findAllLoansByCustomerIdAndAccountId(customerId, accountId)
                .collectList()
                .map(SearchLoansResponse::new)
                .map(this::toResponseEntity);
    }

    @PostMapping("/loan")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Loan>> createLoan(@RequestBody CreateLoanRequest createLoanRequest){
        return loanService.createLoan(createLoanRequest)
                .map(this::toResponseEntity);

    }

    @DeleteMapping("/loan/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> deleteLoan(@PathVariable("id") UUID paymentId){
        return loanService.deleteLoan(paymentId)
                .then(Mono.just(toResponseEntity(paymentId)));
    }

    @GetMapping("/loan/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Loan>> getLoan(@PathVariable("id") UUID paymentId){
        return loanService
                .getLoan(paymentId)
                .map(this::toResponseEntity);
    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
