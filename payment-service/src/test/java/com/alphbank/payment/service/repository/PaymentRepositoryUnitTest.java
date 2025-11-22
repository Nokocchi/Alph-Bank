package com.alphbank.payment.service.repository;

import com.alphbank.payment.service.repository.model.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest
public class PaymentRepositoryUnitTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void testClearBasketIdOnPaymentsMethod() {

        UUID basketId = UUID.randomUUID();

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .basketId(basketId)
                .fromAccountIban("a")
                .recipientIban("aaa")
                .amount(BigDecimal.ONE)
                .currency("SEK")
                .build();

        PaymentEntity savedEntity = paymentRepository.save(paymentEntity).block();
        UUID id = savedEntity.getId();

        StepVerifier.create(paymentRepository.findById(id))
                .expectNextCount(1)
                .assertNext(entity -> assertThat(entity.getBasketId()).isEqualTo(basketId));

        StepVerifier.create(paymentRepository.clearBasketIdOfPaymentsWithBasketId(basketId))
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(paymentRepository.findById(id))
                .expectNextCount(1)
                .assertNext(entity -> assertThat(entity.getBasketId()).isNull());
    }
}
