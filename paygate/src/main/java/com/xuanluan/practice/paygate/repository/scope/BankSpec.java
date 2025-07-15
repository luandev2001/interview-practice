package com.xuanluan.practice.paygate.repository.scope;

import com.xuanluan.practice.paygate.model.entity.Bank;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class BankSpec {
    public static Specification<Bank> active() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<Bank> byCode(Collection<String> codes) {
        return (root, query, cb) -> root.get("code").in(codes);
    }

    public static Specification<Bank> activeWithCode(Collection<String> codes) {
        return active().and(byCode(codes));
    }
}
