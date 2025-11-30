package com.alphbank.core.loan.rest;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.loan.service.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
@Tag(name = "Loan", description = "Description")
public class LoanController {

    private final LoanService loanService;
    private final JsonLog jsonLog;
/*
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SearchLoansResponse> searchLoans(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID accountId) {
        log.info("Search loans by customerId {}, accountId {}", customerId, accountId);
        return loanService.findAllLoansByCustomerIdOrAccountId(customerId, accountId)
                .collectList()
                .map(SearchLoansResponse::new)
                .doOnNext(response -> log.info("Returning loans {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error searching loans", e));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Loan> createLoan(@RequestBody CreateLoanRequest createLoanRequest) {
        log.info("Creating loan {}", jsonLog.format(createLoanRequest));
        return loanService.createLoan(createLoanRequest)
                .doOnNext(response -> log.info("Returning newly created loan {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error creating loan", e));

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UUID> deleteLoan(@PathVariable("id") UUID loanId) {
        log.info("Deleting loan with id {}", loanId);
        return loanService.deleteLoan(loanId)
                .doOnSuccess(response -> log.info("Deleted loan with id {}", loanId))
                .doOnError(e -> log.error("Error deleting loan with id " + loanId, e))
                .thenReturn(loanId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Loan> getLoan(@PathVariable("id") UUID loanId) {
        log.info("Getting loan with id {}", loanId);
        return loanService
                .getLoan(loanId)
                .doOnNext(response -> log.info("Returning loan {}", jsonLog.format(response)))
                .doOnError(e -> log.error("Error getting loan with id " + loanId, e));
    }*/

}
