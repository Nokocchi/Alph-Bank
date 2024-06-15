package com.alphbank.corepaymentservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LogFilter implements WebFilter {
    Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(new PayloadServerWebExchangeDecorator(exchange));
    }

    /*
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .doOnEach(signal -> {
                    System.out.print("Shiiiiiii");
                    if (signal.isOnComplete() || signal.isOnError()) {
                        log.info(
                                "Metadata: requestId: {}, ip: {}, method: {},path :{}, headers: {}, response :{}",
                                exchange.getRequest().getId(), exchange.getRequest().getRemoteAddress(),
                                exchange.getRequest().getMethod(), exchange.getRequest().getPath(),
                                exchange.getRequest().getHeaders().entrySet()
                                        .stream().filter(stringListEntry -> !stringListEntry.getKey().equals("Authorization")).toList(),
                                exchange.getResponse().getStatusCode()
                        );
                    }
                });
    }

     */
}