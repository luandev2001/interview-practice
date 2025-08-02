package com.xuanluan.practice.paygate.base;

import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.repository.IBankRepository;
import com.xuanluan.practice.paygate.repository.IPaymentMethodRepository;
import com.xuanluan.practice.paygate.repository.scope.BankSpec;
import com.xuanluan.practice.paygate.repository.scope.PaymentMethodSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DataSetupTest {
    @Autowired
    private IPaymentMethodRepository paymentMethodRepository;
    @Autowired
    private IBankRepository bankRepository;

    public PaymentMethod createVNPayMethod() {
        var codes = List.of(PaymentConstant.Method.Code.vnpay.name());
        return paymentMethodRepository.findOne(PaymentMethodSpec.activeWithCode(codes))
                .orElseGet(() -> paymentMethodRepository.saveAndFlush(buildPaymentMethod()));
    }

    public Bank createBank() {
        return bankRepository.findOne(BankSpec.activeWithCode(List.of("NCB"))).orElseGet(() -> {
            Bank b = new Bank();
            b.setCode("NCB");
            b.setName("Ngan hang NCB");
            return bankRepository.saveAndFlush(b);
        });
    }

    public PaymentMethod buildPaymentMethod() {
        PaymentMethod pm = new PaymentMethod();
        pm.setCode(PaymentConstant.Method.Code.vnpay);
        pm.setName("VNPay");
        pm.setDepositFeePercent(new BigDecimal("1.5"));
        pm.setDepositFee(new BigDecimal("1000"));
        return pm;
    }

    public Payment buildPayment() {
        var payment = new Payment();
        payment.setUserId(UUID.randomUUID());
        payment.setAmount(new BigDecimal(10000));
        payment.setDescription("Test payment");
        payment.setStatus(PaymentConstant.Status.PENDING);
        payment.setCreatedAt(Instant.now());
        return payment;
    }
}
