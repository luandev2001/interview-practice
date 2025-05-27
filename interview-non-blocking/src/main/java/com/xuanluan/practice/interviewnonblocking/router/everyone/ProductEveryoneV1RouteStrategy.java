package com.xuanluan.practice.interviewnonblocking.router.everyone;

import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.request.ProductRequest;
import com.xuanluan.practice.interviewnonblocking.model.response.WrapperResponse;
import com.xuanluan.practice.interviewnonblocking.router.IEveryoneRouteStrategy;
import com.xuanluan.practice.interviewnonblocking.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@RequiredArgsConstructor
@Component
public class ProductEveryoneV1RouteStrategy implements IEveryoneRouteStrategy {
    private final IProductService productService;

    @Override
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.nest(
                path("products"),
                RouterFunctions.route(POST(""), this::create)
                        .andRoute(GET("/{id}"), this::getById)
        );
    }

    private Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .flatMap(body ->
                        productService.create(body)
                                .flatMap(product -> ServerResponse.ok().bodyValue(WrapperResponse.<Product>builder()
                                        .message("CREATED!!!")
                                        .data(product)
                                        .build()))
                );
    }

    private Mono<ServerResponse> getById(ServerRequest request) {
        return productService.getById(Long.parseLong(request.pathVariable("id")))
                .flatMap(product -> ServerResponse.ok().bodyValue(WrapperResponse.<Product>builder().data(product).build()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(new WrapperResponse<>()));
    }
}
