package com.alphbank.core.unit.service.payment;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.model.Account;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.payment.rest.error.model.PaymentNotFoundException;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.model.Payment;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.PeriodicPaymentRepository;
import com.alphbank.core.payment.service.repository.model.PaymentEntity;
import com.alphbank.core.shared.TransactionService;
import com.alphbank.core.unit.service.UnitTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTest extends UnitTestBase {

    @InjectMocks
    PaymentService paymentService;

    @Mock
    AccountService accountService;

    @Mock
    TransactionService transactionService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PeriodicPaymentRepository periodicPaymentRepository;

    @Mock
    AccountRepository accountRepository;

    @Test
    void createPaymentWithoutScheduledDateTimeExecutesInstantly() {
        Account fromAccount = createAccount(IBAN_1);
        Account recipientAccount = createAccount(IBAN_2);
        Payment payment = createPayment(fromAccount.getId(), recipientAccount.getIban(), null);

        setupRecipientAccountForPayment(payment, recipientAccount);
        setupFromAccountForPayment(payment, fromAccount);
        whenPaymentRepositorySaveReturnArgument();
        whenExecutePaymentDoNothing();

        StepVerifier.create(paymentService.createPayment(payment))
                .expectNextCount(1)
                .verifyComplete();

        verify(transactionService, times(1))
                .executePaymentOnAccounts(
                        argThat(acc -> acc.getIban().equals(recipientAccount.getIban())),
                        argThat(acc -> acc.getId().equals(fromAccount.getId())),
                        any(PaymentEntity.class));
    }

    @Test
    void createPaymentWithScheduledDateTimeInThePastExecutesInstantly() {
        Account fromAccount = createAccount(IBAN_1);
        Account recipientAccount = createAccount(IBAN_2);
        Payment payment = createPayment(fromAccount.getId(), recipientAccount.getIban(), LocalDateTime.now().minusDays(1));

        setupRecipientAccountForPayment(payment, recipientAccount);
        setupFromAccountForPayment(payment, fromAccount);
        whenPaymentRepositorySaveReturnArgument();
        whenExecutePaymentDoNothing();

        StepVerifier.create(paymentService.createPayment(payment))
                .expectNextCount(1)
                .verifyComplete();

        verify(transactionService, times(1))
                .executePaymentOnAccounts(
                        argThat(acc -> acc.getIban().equals(recipientAccount.getIban())),
                        argThat(acc -> acc.getId().equals(fromAccount.getId())),
                        any(PaymentEntity.class));
    }

    @Test
    void createPaymentWithScheduledDateTimeExecutesLater() {
        Account fromAccount = createAccount(IBAN_1);
        Account recipientAccount = createAccount(IBAN_2);
        Payment payment = createPayment(fromAccount.getId(), recipientAccount.getIban(), LocalDateTime.now().plusDays(1));

        setupRecipientAccountForPayment(payment, recipientAccount);
        whenPaymentRepositorySaveReturnArgument();

        StepVerifier.create(paymentService.createPayment(payment))
                .expectNextCount(1)
                .verifyComplete();

        verify(transactionService, never())
                .executePaymentOnAccounts(eq(recipientAccount), eq(fromAccount), any(PaymentEntity.class));
    }

    @Test
    void paymentNotFoundException(){
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId))
                .thenReturn(Mono.empty());

        StepVerifier.create(paymentService.getPayment(paymentId))
                .verifyError(PaymentNotFoundException.class);
    }

    private void whenExecutePaymentDoNothing() {
        when(transactionService.executePaymentOnAccounts(any(Account.class), any(Account.class), any(PaymentEntity.class)))
                .thenReturn(Mono.empty());
    }

    private void whenPaymentRepositorySaveReturnArgument() {
        when(paymentRepository.save(any(PaymentEntity.class)))
                .then(invocation -> Mono.just(invocation.getArgument(0)));
    }

    void setupRecipientAccountForPayment(Payment payment, Account recipientAccount){
        when(accountService.getAccountByIban(eq(payment.getRecipientIban())))
                .thenReturn(Mono.just(recipientAccount));
    }

    void setupFromAccountForPayment(Payment payment, Account fromAccount){
        when(accountService.getAccount(eq(payment.getFromAccountId())))
                .thenReturn(Mono.just(fromAccount));
    }

}
