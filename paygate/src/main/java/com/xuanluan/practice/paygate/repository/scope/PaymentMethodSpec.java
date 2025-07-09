package com.xuanluan.practice.paygate.repository.scope;

import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class PaymentMethodSpec {
    public static Specification<PaymentMethod> active() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<PaymentMethod> byCode(Collection<String> codes) {
        return (root, query, cb) -> root.get("code").in(codes);
    }

    public static Specification<PaymentMethod> activeWithCode(Collection<String> codes) {
        return active().and(byCode(codes));
    }
}
