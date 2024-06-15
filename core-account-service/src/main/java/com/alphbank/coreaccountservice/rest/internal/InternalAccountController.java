package com.alphbank.coreaccountservice.rest.internal;

import com.alphbank.coreaccountservice.rest.internal.model.AccountTransferRequest;
import com.alphbank.coreaccountservice.rest.internal.model.LoanPayoutRequest;
import com.alphbank.coreaccountservice.rest.model.Account;
import com.alphbank.coreaccountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

// Only used within the core services
@CrossOrigin
@RestController
@RequiredArgsConstructor
@Hidden
@RequestMapping("/internal_account")
public class InternalAccountController {

    private final AccountService accountService;

    @PostMapping("/account_transfer")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> transferBetweenAccounts(@RequestBody @Valid AccountTransferRequest accountTransferRequest){
        return accountService.transferBetweenAccounts(accountTransferRequest)
                .then(Mono.just(toResponseEntity()));

    }

    @PostMapping("/loan_payout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> payoutLoan(@RequestBody LoanPayoutRequest loanPayoutRequest){
        return accountService.payoutLoan(loanPayoutRequest)
                .then(Mono.just(toResponseEntity()));

    }

    private ResponseEntity<String> toResponseEntity() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body("Yo");
    }

}
