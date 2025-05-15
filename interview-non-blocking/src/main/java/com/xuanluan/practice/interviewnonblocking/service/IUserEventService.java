package com.xuanluan.practice.interviewnonblocking.service;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserEventService extends ICreateService<UserEvent, UserEventRequest> {
    Mono<UserEvent> getById(UUID id);
}
