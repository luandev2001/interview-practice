package com.xuanluan.practice.paygate.repository;

import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    Optional<PaymentMethod> findFirstByIdAndCode(UUID id, String code);
}
