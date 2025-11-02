package com.alph.core.customer;

import com.alph.core.ContractBase;
import com.alphbank.commons.impl.JsonLog;
import com.alphbank.core.account.service.repository.AccountRepository;
import com.alphbank.core.customer.rest.CustomerController;
import com.alphbank.core.customer.rest.model.Address;
import com.alphbank.core.customer.rest.model.Customer;
import com.alphbank.core.customer.service.CustomerService;
import com.alphbank.core.customer.service.repository.AddressRepository;
import com.alphbank.core.customer.service.repository.CustomerRepository;
import com.alphbank.core.loan.service.repository.LoanRepository;
import com.alphbank.core.payment.service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class CustomerBase extends ContractBase {

    @MockitoBean
    CustomerService customerService;

    @MockitoBean
    AddressRepository addressRepository;

    @MockitoBean
    CustomerRepository customerRepository;

    @MockitoBean
    AccountRepository accountRepository;

    @MockitoBean
    PaymentRepository paymentRepository;

    @MockitoBean
    LoanRepository loanRepository;

    @Autowired
    JsonLog jsonLog;

    @BeforeEach
    void beforeEach(){
        Customer customer = new Customer(UUID.fromString("509c0b04-b1f4-4b72-bac9-3e50e9fbcee4"), new Address("street", "city", "country"), "phone", "nationalId", "firstName", "lastName", Locale.of("sv", "SE"));
        when(customerService.findAllCustomers()).thenReturn(Flux.just(customer));
    }
}
