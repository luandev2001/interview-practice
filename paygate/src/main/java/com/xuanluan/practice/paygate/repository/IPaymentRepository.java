package com.xuanluan.practice.paygate.repository;

import com.xuanluan.practice.paygate.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPaymentRepository extends JpaRepository<Payment, UUID> {
}
