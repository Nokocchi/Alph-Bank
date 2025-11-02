package com.alph.core;

import com.alphbank.core.Application;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

// TODO: Is it really necessary to start a whole Application Context and webserver when we only test the Controllers and mock everything else? Can I not just do
//  something like @WebFluxTest + @AutoConfigureWebTestClient + @ContextConfiguration + @ExtendWith(SpringExtension.class)?
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ContractBase {

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void beforeEach() {
        RestAssuredWebTestClient.applicationContextSetup(context);
    }
}
