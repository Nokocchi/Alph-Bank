package com.alphbank.core.unit.service.payment.repository;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.integration.config.TestContainersConfiguration;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import com.alphbank.core.shared.TransactionService;
import com.alphbank.core.unit.service.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import({PaymentService.class, TestContainersConfiguration.class})
@DataR2dbcTest
public class PaymentRepositoryTransactionTest extends UnitTestBase {

    @MockitoBean
    TransactionService transactionService;

    @MockitoBean
    AccountService accountService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;


    @BeforeEach
    void setup() {
        paymentRepository.deleteAll().block();
    }

    @Test
    void testThatPaymentIsRolledBakIfTransactionCreationFails() {
        Account fromAccount = createAccount(IBAN_1);
        Account recipientAccount = createAccount(IBAN_2);
        Payment payment = createPayment(fromAccount.getId(), recipientAccount.getIban(), null);

        when(accountService.getAccountByIban(eq(payment.getRecipientIban())))
                .thenReturn(Mono.just(recipientAccount));

        when(accountService.getAccount(eq(fromAccount.getId())))
                .thenReturn(Mono.just(fromAccount));

        when(transactionService.executePaymentOnAccounts(any(Account.class), any(Account.class), any(PaymentEntity.class)))
                .thenThrow(DataIntegrityViolationException.class);

        StepVerifier.create(paymentService.createPayment(payment))
                .verifyError(DataIntegrityViolationException.class);

        StepVerifier.create(paymentRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

}
