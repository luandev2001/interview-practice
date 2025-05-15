package com.xuanluan.practice.interviewnonblocking.service.imp;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IUserEventRepository;
import com.xuanluan.practice.interviewnonblocking.service.IUserEventService;
import com.xuanluan.practice.interviewnonblocking.service.mapper.IUserEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserEventServiceImp implements IUserEventService {
    private final IUserEventRepository userEventRepository;
    private final IUserEventMapper userEventMapper;

    @Override
    public Mono<UserEvent> create(UserEventRequest request) {
        return userEventRepository.save(userEventMapper.toUserEvent(request));
    }

    @Override
    public Mono<UserEvent> getById(UUID id) {
        return userEventRepository.findById(id);
    }
}
