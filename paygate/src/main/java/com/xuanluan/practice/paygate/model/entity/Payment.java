package com.xuanluan.practice.paygate.model.entity;

import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@DynamicInsert
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
    private PaymentConstant.Status status;
}
