package com.alphbank.core.unit.service.account.repository;

import com.alphbank.core.account.service.AccountService;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.shared.TransactionService;
import com.alphbank.core.unit.service.account.AccountUnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(AccountService.class)
@DataR2dbcTest
public class AccountRepositoryTransactionTest extends AccountUnitTestBase {

    @Autowired
    TransactionService transactionService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setup(){
        customerRepository.deleteAll().block();
        accountRepository.deleteAll().block();
    }

}
