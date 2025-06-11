package com.xuanluan.practice.interviewnonblocking.repository;

import com.xuanluan.practice.interviewnonblocking.model.entity.OutboxEvent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface IOutboxEventRepository extends R2dbcRepository<OutboxEvent, UUID> {
    Flux<OutboxEvent> findTop100ByProcessedAtIsNullAndRetriesLessThanOrderByUpdatedAtAsc(int maxRetries);
}
