package com.xuanluan.practice.interviewnonblocking.repository;

import com.xuanluan.practice.interviewnonblocking.model.entity.OutboxEvent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface IOutboxEventRepository extends R2dbcRepository<OutboxEvent, UUID> {
}
