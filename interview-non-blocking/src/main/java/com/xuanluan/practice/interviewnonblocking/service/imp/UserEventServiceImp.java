package com.xuanluan.practice.interviewnonblocking.service.imp;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IUserEventRepository;
import com.xuanluan.practice.interviewnonblocking.service.IUserEventService;
import com.xuanluan.practice.interviewnonblocking.service.mapper.UserEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserEventServiceImp implements IUserEventService {
    private final IUserEventRepository userEventRepository;
    private final UserEventMapper userEventMapper;

    @Override
    public Mono<UserEvent> create(UserEventRequest request) {
        return userEventRepository.save(userEventMapper.toUserEvent(request));
    }
}
