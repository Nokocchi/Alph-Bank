package com.alphbank.payment.integration;

import com.alphbank.payment.service.repository.BasketRepository;
import com.alphbank.payment.service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.berlingroup.rest.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class PaymentControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    WebTestClient webClient;

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    public void setup() {
        basketRepository.deleteAll().block();
        paymentRepository.deleteAll().block();
    }

    @Test
    public void happyPathPSD2SigningBasketFlowWithOnePeriodicPayment() {

        stubFor(post("/signing")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "signingSessionId": "70599aa9-1f90-4c21-b366-94f049649652",
                                  "signingUrl": "http://localhost:10000/sign"
                                }
                                """)
                ));

        PeriodicPaymentInitiationJsonDTO paymentInitiation = PeriodicPaymentInitiationJsonDTO.builder()
                .frequency(FrequencyCodeDTO.DAILY)
                .creditorName("Mr Moneybags")
                .debtorAccount(AccountReferenceDTO.builder()
                        .iban("FR7612345987650123456789014")
                        .build())
                .creditorAccount(AccountReferenceDTO.builder()
                        .iban("FR7612345987650123456789014")
                        .build())
                .instructedAmount(AmountDTO.builder()
                        .amount("46")
                        .currency("SEK")
                        .build())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .build();

        PaymentInitiationRequestResponse201DTO paymentResponse = webClient.post()
                .uri(uri -> uri.path("/v1/periodic-payments/sepa-credit-transfers").build())
                .bodyValue(paymentInitiation)
                .headers(httpHeaders -> {
                    httpHeaders.add("X-Request-ID", "2a2534c9-5e1d-4e40-9275-ae8e0ddc1d03");
                    httpHeaders.add("PSU-IP-Address", "1.1.1.1");
                })
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(PaymentInitiationRequestResponse201DTO.class)
                .returnResult()
                .getResponseBody();

        String paymentId = paymentResponse.getPaymentId();

        SigningBasketDTO basketRequest = SigningBasketDTO.builder()
                .paymentIds(List.of(paymentId))
                .build();

        SigningBasketResponse201DTO basketResponse = webClient.post()
                .uri(uri -> uri.path("/v1/signing-baskets").build())
                .bodyValue(basketRequest)
                .headers(httpHeaders -> {
                    httpHeaders.add("X-Request-ID", "2a2534c9-5e1d-4e40-9275-ae8e0ddc1d03");
                    httpHeaders.add("PSU-IP-Address", "1.1.1.1");
                    httpHeaders.add("TPP-Redirect-URI", "http://success.com");
                    httpHeaders.add("TPP-Nok-Redirect-URI", "http://failure.com");
                    httpHeaders.add("PSU-Accept-Language", "sv");
                })
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(SigningBasketResponse201DTO.class)
                .returnResult()
                .getResponseBody();

        String basketId = basketResponse.getBasketId();

        StartScaprocessResponseDTO startAuthorizationResponse = webClient.post()
                .uri(uri -> uri.path("/v1/signing-baskets/{basketId}/authorisations").build(basketId))
                .headers(httpHeaders -> {
                    httpHeaders.add("X-Request-ID", "2a2534c9-5e1d-4e40-9275-ae8e0ddc1d03");
                    httpHeaders.add("PSU-Accept-Language", "sv");
                })
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(StartScaprocessResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(startAuthorizationResponse.getAuthorisationId()).isEqualTo(basketId);
    }
}