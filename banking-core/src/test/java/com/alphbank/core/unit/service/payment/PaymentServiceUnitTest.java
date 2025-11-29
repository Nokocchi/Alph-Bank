package com.alphbank.core.unit.service.payment;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.payment.service.PaymentService;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import com.alphbank.core.payment.service.repository.PeriodicPaymentRepository;
import com.alphbank.core.shared.TransactionService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTest {

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


}
