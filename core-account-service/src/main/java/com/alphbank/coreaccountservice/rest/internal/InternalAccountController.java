package com.alphbank.coreaccountservice.rest.internal;

import com.alphbank.coreaccountservice.rest.internal.model.AccountTransferRequest;
import com.alphbank.coreaccountservice.rest.internal.model.LoanPayoutRequest;
import com.alphbank.coreaccountservice.rest.model.Account;
import com.alphbank.coreaccountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

// Only used within the core services - imagine stricter authorization or firewall whitelisting
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal_account")
public class InternalAccountController {

    private final AccountService accountService;

    @PostMapping(value = "/account_transfer", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> transferBetweenAccounts(@RequestBody @Valid AccountTransferRequest accountTransferRequest){
        System.out.println("Internal transfer");
        return accountService.transferBetweenAccounts(accountTransferRequest)
                .then(Mono.just(toResponseEntity()));
    }

    @GetMapping("/account_transfer")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> test() {
        System.out.println("Internal transfer GET TEST");
        return Mono.just(ResponseEntity.ok("DJUSS"));
    }

    @PostMapping("/loan_payout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> payoutLoan(@RequestBody LoanPayoutRequest loanPayoutRequest) {
        return accountService.payoutLoan(loanPayoutRequest)
                .then(Mono.just(toResponseEntity()));

    }

    private ResponseEntity<String> toResponseEntity() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body("Yo");
    }

}
