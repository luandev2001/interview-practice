package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

public class BaseEntityListener {
    @PrePersist
    public void prePersist(BaseEntity entity) {
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
    }
}
