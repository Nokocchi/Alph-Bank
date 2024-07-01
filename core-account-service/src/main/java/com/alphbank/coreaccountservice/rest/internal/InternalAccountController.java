package com.alphbank.coreaccountservice.rest.internal;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.coreaccountservice.rest.internal.model.AccountTransferRequest;
import com.alphbank.coreaccountservice.rest.internal.model.LoanPayoutRequest;
import com.alphbank.coreaccountservice.rest.model.Account;
import com.alphbank.coreaccountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

// Only used within the core services - imagine stricter authorization or firewall whitelisting
@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal_account")
public class InternalAccountController {

    private final AccountService accountService;
    private final JsonLog jsonLog;

    @PostMapping(value = "/account_transfer", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> transferBetweenAccounts(@RequestBody @Valid AccountTransferRequest accountTransferRequest){
        log.info("Transfer between accounts {}", jsonLog.format(accountTransferRequest));
        return accountService.transferBetweenAccounts(accountTransferRequest)
                .doOnSuccess(response -> log.info("Executed payment {}, transferred {} from account {} to account with IBAN {}", accountTransferRequest.paymentReference(), accountTransferRequest.remittanceAmount(), accountTransferRequest.debtorAccountId(), accountTransferRequest.recipientIban()))
                .doOnError(e -> log.error("Error executing payment", e))
                .then(Mono.just(toResponseEntity(accountTransferRequest.paymentReference())));
    }

    @PostMapping("/loan_payout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<UUID>> payoutLoan(@RequestBody LoanPayoutRequest loanPayoutRequest) {
        log.info("Pay out loan {}", jsonLog.format(loanPayoutRequest));
        return accountService.payoutLoan(loanPayoutRequest)
                .doOnSuccess(response -> log.info("Paid out loan of {} with id {} to {}", loanPayoutRequest.remittanceAmount(), loanPayoutRequest.loanReference(), loanPayoutRequest.debtorAccountId()))
                .doOnError(e -> log.error("Error paying out loan", e))
                .then(Mono.just(toResponseEntity(loanPayoutRequest.loanReference())));

    }

    private <T> ResponseEntity<T> toResponseEntity(T responseBody) {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(responseBody);
    }

}
