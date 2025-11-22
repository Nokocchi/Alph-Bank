package com.alphbank.core.payment;

import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.payment.rest.PaymentController;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.rest.model.CreatePaymentRequestDTO;
import com.alphbank.core.rest.model.MonetaryAmountDTO;
import com.alphbank.core.rest.model.PaymentDTO;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentBase {

    @Mock
    PaymentService paymentService;

    @Mock
    JsonLog jsonLog;

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.standaloneSetup(new PaymentController(paymentService, jsonLog));

        UUID uuid = UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4");
        PaymentDTO payment = PaymentDTO.builder()
                .paymentId(uuid)
                .fromCustomerId(uuid)
                .fromAccountId(uuid)
                .amount(MonetaryAmountDTO.builder()
                        .amount(BigDecimal.valueOf(22))
                        .currency("SEK")
                        .build())
                .recipientIban("string")
                .recipientAccountId(uuid)
                .messageToSelf("string")
                .messageToRecipient("string")
                .executedDateTime(null)
                .scheduledDateTime(null)
                .build();
        when(paymentService.createPayment(any(CreatePaymentRequestDTO.class))).thenReturn(Mono.just(payment));
    }
}
