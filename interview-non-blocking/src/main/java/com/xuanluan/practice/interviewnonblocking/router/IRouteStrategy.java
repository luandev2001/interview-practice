package com.xuanluan.practice.interviewnonblocking.router;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public interface IRouteStrategy {
    RouterFunction<ServerResponse> route();
}
