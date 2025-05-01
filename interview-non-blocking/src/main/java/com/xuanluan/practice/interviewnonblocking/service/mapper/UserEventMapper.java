package com.xuanluan.practice.interviewnonblocking.service.mapper;

import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEventMapper {
    UserEvent toUserEvent(UserEventRequest userEventRequest);
}
