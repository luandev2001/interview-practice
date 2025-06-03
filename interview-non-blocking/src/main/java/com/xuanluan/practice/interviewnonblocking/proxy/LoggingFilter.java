package com.xuanluan.practice.interviewnonblocking.proxy;

import com.xuanluan.practice.interviewnonblocking.constant.RequestConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("[LOGGING FILTER] {} {}", request.getMethod(), request.getURI());

        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    String body = new String(bytes, StandardCharsets.UTF_8);
                    log.info("[LOGGING FILTER] Request Body: {}", sanitizeBody(body));

                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        return Mono.just(buffer);
                    });

                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String sanitizeBody(String body) {
        return RequestConstant.Body.SENSITIVE_AND_SPACE_PATTERN.matcher(body).replaceAll(m ->
                m.group().startsWith("\"") ? "\"" + m.group(1) + "\":\"***\"" : ""
        );
    }
}