package com.alphbank.payment.integration;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.payment.rest.model.request.CreatePaymentRequest;
import com.alphbank.payment.rest.model.request.SetupSigningSessionRestRequest;
import com.alphbank.payment.rest.model.response.SetupSigningSessionRestResponse;
import com.alphbank.payment.service.PaymentService;
import com.alphbank.payment.service.repository.BasketRepository;
import com.alphbank.payment.service.repository.PaymentRepository;
import com.alphbank.payment.service.repository.model.SigningBasketEntity;
import com.alphbank.payment.service.repository.model.PaymentEntity;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class PaymentControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    PaymentService paymentService;

    @Autowired
    WebTestClient webClient;

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    JsonLog jsonLog;

    @BeforeEach
    public void setup() {
        basketRepository.deleteAll().block();
        paymentRepository.deleteAll().block();
    }

    @Test
    public void testIt() throws InterruptedException {
        UUID basketId = UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4");
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(basketId, basketId, "string", "string", "string", Money.of(22, "SEK"), LocalDateTime.parse("2015-08-04T10:11:30"));

        PaymentEntity payment = PaymentEntity.from(basketId, paymentRequest);
        log.info("TEST STARTS HERE");
        paymentRepository.save(payment).block();
        Thread.sleep(10000);
        // TODO: WHY DOES THIS NOT LOG. Seems that if I pause execution and Evaluate Expression and then save.block() there, and check, then it works..
        // Is it because the transaction is not committed in the test?
        // Does the .save() work if done after the controller? (Find example of this working)
        // If this is an integration test, maybe I shouldn't mess with the repositories directly..
        paymentRepository.findAll().collectList().doOnNext(l -> l.forEach(s -> log.info(jsonLog.format(s))));

        SigningBasketEntity basket = SigningBasketEntity.from(basketId);
        basketRepository.save(basket);

        SetupSigningSessionRestRequest basketRequest = new SetupSigningSessionRestRequest(basketId,
                "12341234",
                Locale.of("sv", "SE"),
                "http://localhost:10000/signing_completed",
                "http://localhost:10000/signing_failed"
        );

        stubFor(post("/signing")
                /*.withRequestBody(equalToJson(
                        """

                        """))*/
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        //.withHeader()
                        .withBody("""
                                {
                                  "signingSessionId": "70599aa9-1f90-4c21-b366-94f049649652",
                                  "signingUrl": "http://localhost:10000/sign"
                                }
                                """)
                ));

        SetupSigningSessionRestResponse response = webClient.post()
                .uri(uri -> uri.path("/basket/{basketId}/authorize")
                        .build("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"))
                .bodyValue(basketRequest)
                .exchange()
                .expectBody(SetupSigningSessionRestResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
    }
}