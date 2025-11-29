package com.alphbank.core.integration;


import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.account.service.repository.TransactionRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.integration.config.TestContainersConfiguration;
import com.alphbank.core.loan.service.repository.LoanRepository;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.rest.model.*;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;

// Random Port auto-configures a WebTestClient that we can use. The random port ensures that we won't run into port bind conflicts when running multiple test classes
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersConfiguration.class})
@ActiveProfiles("test")
public class IntegrationTestBase {

    protected static String NATIONAL_ID_1 = "12345";
    protected static String NATIONAL_ID_2 = "56789";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    protected WebTestClient webClient;

    @BeforeEach
    public void cleanup() {
        customerRepository.deleteAll().block();
        accountRepository.deleteAll().block();
        paymentRepository.deleteAll().block();
        loanRepository.deleteAll().block();
        transactionRepository.deleteAll().block();
    }

    protected CreateCustomerRequestDTO createCustomerRequest(String nationalId) {
        return CreateCustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .nationalId(nationalId)
                .language("sv")
                .countryCode("SE")
                .address(AddressDTO.builder()
                        .streetAddress("Street")
                        .city("City")
                        .country("Country")
                        .build())
                .build();
    }

    protected CreateAccountRequestDTO createAccountRequest(UUID customerId) {
        return CreateAccountRequestDTO.builder()
                .accountName("Account 2")
                .customerId(customerId)
                .build();
    }

    protected CreatePaymentRequestDTO createPaymentRequest(UUID fromAccountId, String recipientAccountIBan) {
        return CreatePaymentRequestDTO.builder()
                .fromAccountId(fromAccountId)
                .messageToRecipient("To you")
                .messageToSelf("From me")
                .recipientIban(recipientAccountIBan)
                .recipientName("Jane Doe")
                .scheduledDateTime(null)
                .amount(MonetaryAmountDTO.builder()
                        .amount(BigDecimal.valueOf(22))
                        .currency("SEK")
                        .build())
                .build();
    }

}
