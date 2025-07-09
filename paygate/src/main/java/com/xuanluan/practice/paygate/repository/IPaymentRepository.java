package com.xuanluan.practice.paygate.repository;

import com.xuanluan.practice.paygate.model.entity.Payment;

import java.util.UUID;

public interface IPaymentRepository extends IMultipleJpaRepository<Payment, UUID> {
}
