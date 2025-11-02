package com.alphbank.core;

import com.alphbank.core.integration.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
