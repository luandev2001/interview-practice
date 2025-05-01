package com.xuanluan.practice.interviewnonblocking.service;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import reactor.core.publisher.Mono;

public interface IUserEventService {
    Mono<UserEvent> create(UserEventRequest request);
}
