package com.xuanluan.practice.interviewnonblocking.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class BaseEntity<T extends Serializable> {
    @Id
    private T id;
    private Instant createdAt;
    private Instant updatedAt;
}