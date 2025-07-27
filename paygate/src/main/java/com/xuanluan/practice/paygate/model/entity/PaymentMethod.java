package com.xuanluan.practice.paygate.model.entity;

import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
public class PaymentMethod extends BaseEntity {
    private String name;
    @Enumerated(EnumType.STRING)
    private PaymentConstant.Method.Code code;
    private boolean isDeleted;
    @OneToMany(mappedBy = "paymentMethod")
    private List<Payment> payments;
    private BigDecimal withdrawalFee;
    private BigDecimal withdrawalFeePercent;
    private BigDecimal depositFee;
    private BigDecimal depositFeePercent;
}
