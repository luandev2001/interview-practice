package com.xuanluan.practice.interviewnonblocking.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@RequiredArgsConstructor
@Configuration
public class EveryoneRouterFunction {
    @Bean
    RouterFunction<ServerResponse> everyoneRoutes(List<IEveryoneRouteStrategy> routeStrategies) {
        RouterFunction<ServerResponse> routerFunction = routeStrategies.stream()
                .map(IRouteStrategy::route).reduce(RouterFunction::and)
                .orElse(route(all(), _ -> ServerResponse.notFound().build()));
        return nest(path("everyone"), routerFunction);
    }
}
