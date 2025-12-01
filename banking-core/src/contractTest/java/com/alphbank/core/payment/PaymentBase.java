package com.alphbank.core.payment;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.Application;
import com.alphbank.core.payment.rest.PaymentController;
import com.alphbank.core.payment.rest.error.PaymentRestErrorHandler;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.model.Payment;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PaymentBase {

    @Mock
    PaymentService paymentService;

    @Mock
    JsonLog jsonLog;

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.standaloneSetup(new PaymentController(paymentService, jsonLog));

        Hooks.onOperatorDebug();

        UUID uuid = UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4");
        Payment payment = Payment.builder()
                .id(uuid)
                .fromAccountId(uuid)
                .amount(Money.of(22, "SEK"))
                .recipientIban("string")
                .messageToSelf("string")
                .messageToRecipient("string")
                .executedDateTime(null)
                .scheduledDateTime(null)
                .build();

        when(paymentService.createPayment(any(Payment.class))).thenReturn(Mono.just(payment));
        when(paymentService.getPayment(any(UUID.class))).thenReturn(Mono.just(payment));
        when(paymentService.deletePayment(any(UUID.class))).thenReturn(Mono.empty());
        when(paymentService.getPendingPaymentsByCustomerId(any(UUID.class))).thenReturn(Flux.just(payment));
    }
}
