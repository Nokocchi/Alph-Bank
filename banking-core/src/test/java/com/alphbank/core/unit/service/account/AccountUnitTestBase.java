package com.alphbank.core.unit.service.account;

import com.alphbank.core.account.service.model.Account;

public class AccountUnitTestBase {


    protected Account createAccount() {
        return Account.builder()
                .build();
    }
}
