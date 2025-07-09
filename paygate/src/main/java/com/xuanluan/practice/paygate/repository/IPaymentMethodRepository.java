package com.xuanluan.practice.paygate.repository;

import com.xuanluan.practice.paygate.model.entity.PaymentMethod;

import java.util.UUID;

public interface IPaymentMethodRepository extends IMultipleJpaRepository<PaymentMethod, UUID> {
}
