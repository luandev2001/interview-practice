package com.xuanluan.practice.interviewnonblocking.service.mapper;

import com.xuanluan.practice.interviewnonblocking.model.entity.BaseEntity;
import com.xuanluan.practice.interviewnonblocking.model.entity.OutboxEvent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOutboxEventMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "eventType", expression = "java(source != null ? source.getClass().getSimpleName() : null)")
    @Mapping(target = "eventId", expression = "java(source != null && source.getId() != null ? String.valueOf(source.getId()) : null)")
    OutboxEvent toOutboxEvent(BaseEntity<?> source);
}
