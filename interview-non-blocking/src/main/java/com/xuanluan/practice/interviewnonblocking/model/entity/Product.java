package com.xuanluan.practice.interviewnonblocking.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table
public class Product extends BaseEntity<Long> {
    private String name;
}
