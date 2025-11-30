package com.alphbank.core.unit.service.payment.repository;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.PeriodicPaymentRepository;
import com.alphbank.core.shared.TransactionService;
import com.alphbank.core.unit.service.payment.PaymentUnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

@Import(PaymentService.class)
@DataR2dbcTest
public class PaymentRepositoryTransactionTest extends PaymentUnitTestBase {

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PeriodicPaymentRepository periodicPaymentRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll().block();
        periodicPaymentRepository.deleteAll().block();
        accountRepository.deleteAll().block();
    }

}
