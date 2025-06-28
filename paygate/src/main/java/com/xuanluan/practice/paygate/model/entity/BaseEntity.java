package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
}
