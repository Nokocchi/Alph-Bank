package com.alphbank.core.integration.payments;

import com.alphbank.core.integration.IntegrationTestBase;
import com.alphbank.core.rest.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentControllerIntegrationSpringConfigTest extends IntegrationTestBase {

    @Test
    void testCreateInstantPaymentHappyPath_verifyAccountBalancesChanged() {
        CustomerDTO debtor = createCustomer(NATIONAL_ID_1);
        CustomerDTO creditor = createCustomer(NATIONAL_ID_2);

        AccountDTO debtorAccount = createAccount(debtor.getId());
        AccountDTO creditorAccount = createAccount(creditor.getId());

        CreatePaymentRequestDTO paymentRequest = createPaymentRequest(debtorAccount.getId(), creditorAccount.getIban());

        // Both accounts have a 0 balance BEFORE the payment is done
        assertThat(debtorAccount.getBalance().getAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(creditorAccount.getBalance().getAmount()).isEqualTo(BigDecimal.ZERO);

        // Send payment to core
        PaymentDTO paymentResponse = createPayment(paymentRequest);

        // Fetch accounts after payment was sent to core
        AccountDTO debtorAccountAfterPayment = getAccount(debtorAccount.getId());
        AccountDTO creditorAccountAfterPayment = getAccount(creditorAccount.getId());

        // Assert that the returned Payment response has correct to/from account info
        assertThat(paymentResponse.getFromAccountId()).isEqualTo(debtorAccount.getId());

        BigDecimal paymentAmount = paymentRequest.getAmount().getAmount();

        // The accounts now have balances of paymentAmount and negative paymentAmount, respectively.
        assertThat(debtorAccountAfterPayment.getBalance().getAmount())
                .isEqualTo(paymentAmount.negate());

        assertThat(creditorAccountAfterPayment.getBalance().getAmount())
                .isEqualTo(paymentAmount);
    }

    @Test
    void testThatTransactionsHaveCorrectData() {
        CustomerDTO debtor = createCustomer(NATIONAL_ID_1);
        CustomerDTO creditor = createCustomer(NATIONAL_ID_2);

        AccountDTO debtorAccount = createAccount(debtor.getId());
        AccountDTO creditorAccount = createAccount(creditor.getId());

        CreatePaymentRequestDTO paymentRequest = createPaymentRequest(debtorAccount.getId(), creditorAccount.getIban());
        BigDecimal paymentAmount = paymentRequest.getAmount().getAmount();

        // Send payment to core
        createPayment(paymentRequest);

        AccountDTO debtorAccountAfterPayment = getAccount(debtorAccount.getId());
        AccountDTO creditorAccountAfterPayment = getAccount(creditorAccount.getId());

        List<TransactionDTO> debtorAccountTransactions = getTransactions(debtorAccount.getId());
        List<TransactionDTO> creditorAccountTransactions = getTransactions(creditorAccount.getId());

        assertThat(debtorAccountTransactions)
                .hasSize(1)
                .first()
                .satisfies(transaction -> {
                    assertThat(transaction.getAmount().getAmount()).isEqualTo(paymentAmount.negate());
                    assertThat(transaction.getNewBalance()).satisfies(newBalance -> {
                        assertThat(newBalance.getAmount()).isEqualTo(paymentAmount.negate());
                        assertThat(newBalance.getAmount()).isEqualTo(debtorAccountAfterPayment.getBalance().getAmount());
                    });
                    assertThat(transaction.getMessage()).isEqualTo(paymentRequest.getMessageToSelf());
                    assertThat(transaction.getExecutedDateTime()).isBefore(LocalDateTime.now());
                });

        assertThat(creditorAccountTransactions)
                .hasSize(1)
                .first()
                .satisfies(transaction -> {
                    assertThat(transaction.getAmount().getAmount()).isEqualTo(paymentAmount);
                    assertThat(transaction.getNewBalance()).satisfies(newBalance -> {
                        assertThat(newBalance.getAmount()).isEqualTo(paymentAmount);
                        assertThat(newBalance.getAmount()).isEqualTo(creditorAccountAfterPayment.getBalance().getAmount());
                    });
                    assertThat(transaction.getMessage()).isEqualTo(paymentRequest.getMessageToRecipient());
                    assertThat(transaction.getExecutedDateTime()).isBefore(LocalDateTime.now());
                });
    }

    private List<TransactionDTO> getTransactions(UUID accountId) {
        return webClient.get()
                .uri(uri -> uri.path("/accounts/{accountId}/transactions").build(accountId))
                .exchange()
                .expectBodyList(TransactionDTO.class)
                .returnResult()
                .getResponseBody();
    }

    private AccountDTO getAccount(UUID accountId) {
        return webClient.get()
                .uri(uri -> uri.path("/accounts/{accountId}").build(accountId))
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();
    }

    private PaymentDTO createPayment(CreatePaymentRequestDTO paymentRequest) {
        return webClient.post()
                .uri(uri -> uri.path("/payments").build())
                .bodyValue(paymentRequest)
                .exchange()
                .expectBody(PaymentDTO.class)
                .returnResult()
                .getResponseBody();
    }

    private AccountDTO createAccount(UUID customerId) {
        return webClient.post()
                .uri(uri -> uri.path("/accounts").build())
                .bodyValue(createAccountRequest(customerId))
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();
    }

    private CustomerDTO createCustomer(String nationalId) {
        return webClient.post()
                .uri(uri -> uri.path("/customers").build())
                .bodyValue(createCustomerRequest(nationalId))
                .exchange()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();
    }


}
