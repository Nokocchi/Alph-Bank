package com.alphbank.core.payment;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.Application;
import com.alphbank.core.integration.config.TestContainersConfiguration;
import com.alphbank.core.payment.rest.PaymentController;
import com.alphbank.core.payment.rest.model.CreatePaymentRequest;
import com.alphbank.core.payment.rest.model.Payment;
import com.alphbank.core.payment.service.PaymentService;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// TODO: Seems that we need the Spring context because otherwise RestAssured does not use the Money jackson module.. Can we somehow autowire only the Spring ObjectMapper
//  and make RestAssured use that one?
//  We also need to manually import the test container configuration even though we don't need it, because Spring refuses to start with that dependency missing
@Import(TestContainersConfiguration.class)
@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class)
public class PaymentBase {

    @MockitoBean
    PaymentService paymentService;

    @Autowired
    JsonLog jsonLog;

    @Autowired
    ApplicationContext context;

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.applicationContextSetup(context);

        UUID uuid = UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4");
        Payment payment = new Payment(
                uuid,
                uuid,
                uuid,
                Money.of(22, "SEK"),
                "string",
                uuid,
                "string",
                "string",
                null,
                null
        );
        when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(Mono.just(payment));
    }
}
