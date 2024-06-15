package com.alphbank.reactivelogging.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.alphbank.coreaccountservice.LogUtils.legalLogMediaTypes;
import static com.alphbank.coreaccountservice.LogUtils.loggingResponse;
import static reactor.core.scheduler.Schedulers.single;

@Slf4j
public class PartnerServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    PartnerServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return super.writeAndFlushWith(body);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        final MediaType contentType = super.getHeaders().getContentType();
        if (legalLogMediaTypes.contains(contentType)) {
            if (body instanceof Mono) {
                final Mono<DataBuffer> monoBody = (Mono<DataBuffer>) body;
                return super.writeWith(monoBody.publishOn(single())
                        .map(dataBuffer -> loggingResponse(log, dataBuffer)));
            } else if (body instanceof Flux) {
                final Flux<DataBuffer> monoBody = (Flux<DataBuffer>) body;
                return super.writeWith(monoBody.publishOn(single())
                        .map(dataBuffer -> loggingResponse(log, dataBuffer)));
            }
        }
        return super.writeWith(body);
    }


}