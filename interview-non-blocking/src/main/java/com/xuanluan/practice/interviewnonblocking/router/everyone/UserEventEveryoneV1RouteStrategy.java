package com.xuanluan.practice.interviewnonblocking.router.everyone;

import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.model.response.WrapperResponse;
import com.xuanluan.practice.interviewnonblocking.router.IEveryoneRouteStrategy;
import com.xuanluan.practice.interviewnonblocking.service.IUserEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@RequiredArgsConstructor
@Component
public class UserEventEveryoneV1RouteStrategy implements IEveryoneRouteStrategy {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final IUserEventService userEventService;

    @Override
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.nest(
                path("user_events"),
                RouterFunctions.route(POST(""), this::create)
                        .andRoute(GET("/{id}"), this::getById)
        );
    }

    /**
     * Why push data to Kafka?
     * - easy to retry, resilient
     * - avoid bottleneck
     * - optimise save to database
     **/
    private Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(UserEventRequest.class)
                .doOnNext(body -> {
                    System.out.println("processing!!!!");
                    kafkaTemplate.send(KafkaConstant.Topic.USER_EVENT, body);
                })
                .flatMap(body -> ServerResponse.ok().bodyValue(WrapperResponse.<UserEventRequest>builder()
                        .message("CREATED!!!")
                        .data(body)
                        .build()));
    }

    private Mono<ServerResponse> getById(ServerRequest request) {
        return userEventService.getById(UUID.fromString(request.pathVariable("id")))
                .flatMap(response -> ServerResponse.ok().bodyValue(WrapperResponse.builder().data(response).build()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(new WrapperResponse<>()));
    }
}
