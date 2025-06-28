package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ExchangeRate extends BaseEntity {
    private String baseCurrency;
    private String quoteCurrency;
    private double rate;
    private boolean isDeleted;
}
