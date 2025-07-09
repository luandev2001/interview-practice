package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.model.exception.EntityLookupException;
import com.xuanluan.practice.paygate.service.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentFactory {
    private final Map<String, IPaymentService> paymentServices;

    public PaymentFactory(List<IPaymentService> paymentServices) {
        this.paymentServices = paymentServices.stream()
                .collect(Collectors.toMap(IPaymentService::getMethodCode, service -> service));
    }

    public IPaymentService get(String methodCode) {
        IPaymentService paymentService = paymentServices.get(methodCode);
        if (paymentService == null) {
            throw EntityLookupException.build(PaymentMethod.class, Map.of("code", methodCode));
        }
        return paymentService;
    }
}
