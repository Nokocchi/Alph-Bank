package com.alphbank.corepaymentservice;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.zalando.jackson.datatype.money.MoneyModule;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import javax.money.MonetaryAmount;

@EnableScheduling
@EnableR2dbcRepositories
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    static {
        SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, org.springdoc.core.converters.models.MonetaryAmount.class);
    }

    @Bean
    public MoneyModule moneyModule() {
        return new MoneyModule();
    }
}
