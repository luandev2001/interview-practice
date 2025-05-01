package com.xuanluan.practice.interviewnonblocking.model.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity<UUID> {
    private String eventType;
    private String eventId;
    private String type;
    private int retries;
    private Json payload;
    private Instant processedAt;
}
