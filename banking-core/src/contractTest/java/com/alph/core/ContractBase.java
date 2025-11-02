package com.alph.core;

import com.alphbank.core.Application;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class ContractBase {

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.applicationContextSetup(context);
    }
}
