//package com.xuanluan.practice.interviewnonblocking.router.v1.everyone;
//
//import com.xuanluan.practice.interviewnonblocking.router.IEveryoneRouteStrategy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
//import static org.springframework.web.reactive.function.server.RequestPredicates.path;
//import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
//
//@RequiredArgsConstructor
//@Component
//public class ProductEveryoneV1RouteStrategy implements IEveryoneRouteStrategy {
//
//    @Override
//    public RouterFunction<ServerResponse> route() {
//        return nest(
//                path("products"),
//                RouterFunctions.route(POST(""), this::create)
//        );
//    }
//
//    @Override
//    public String getVersion() {
//        return "v1";
//    }
//}
