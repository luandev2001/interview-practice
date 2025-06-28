package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.external.VNPayClient;
import com.xuanluan.practice.paygate.model.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VNPayServiceImp extends BaseService {
    private final VNPayClient vnPayClient;

    @Override
    public String getType() {
        return "vnpay";
    }

    @Override
    protected void handleDeposit(Payment payment) {
        vnPayClient.deposit(payment);
    }
}
