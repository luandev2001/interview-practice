package com.xuanluan.practice.paygate.repository;

import com.xuanluan.practice.paygate.model.entity.Bank;

import java.util.Optional;
import java.util.UUID;

public interface IBankRepository extends IMultipleJpaRepository<Bank, UUID> {
    Optional<Bank> findByCode(String code);
}
