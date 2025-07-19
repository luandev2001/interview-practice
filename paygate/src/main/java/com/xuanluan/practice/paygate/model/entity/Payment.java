package com.xuanluan.practice.paygate.model.entity;

import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Payment extends BaseEntity {
    private UUID userId;
    private BigDecimal amount;
    private BigDecimal receivedAmount;
    private BigDecimal fee;
    private String currency;
    private BigDecimal rate;
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentMethod paymentMethod;
    private String description;
    @Enumerated(EnumType.ORDINAL)
    private PaymentConstant.Status status;

    @PrePersist
    private void prePersist() {
        status = PaymentConstant.Status.PENDING;
    }
}
