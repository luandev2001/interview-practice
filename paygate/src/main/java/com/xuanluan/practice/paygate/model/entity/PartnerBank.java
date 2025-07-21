package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PartnerBank extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private Bank bank;
    @OneToOne(fetch = FetchType.LAZY)
    private PaymentMethod paymentMethod;
}
