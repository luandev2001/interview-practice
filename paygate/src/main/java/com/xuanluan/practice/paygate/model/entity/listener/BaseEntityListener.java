package com.xuanluan.practice.paygate.model.entity.listener;

import com.xuanluan.practice.paygate.model.entity.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

public class BaseEntityListener {
    @PrePersist
    public void prePersist(BaseEntity entity) {
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedAt(Instant.now());
    }
}
