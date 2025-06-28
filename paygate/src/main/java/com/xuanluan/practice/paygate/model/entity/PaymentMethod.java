package com.xuanluan.practice.paygate.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class PaymentMethod extends BaseEntity {
    private String name;
    private String code;
    private boolean isDeleted;
    @OneToMany(mappedBy = "paymentMethod")
    private List<Payment> payments;
}
