package com.xuanluan.practice.interviewnonblocking.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table
public class UserEvent extends BaseEntity<UUID>{
    private UUID userId;
    private String eventType;
    private String eventId;
}
