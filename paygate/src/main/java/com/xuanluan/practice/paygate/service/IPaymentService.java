package com.xuanluan.practice.paygate.service;

import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.request.DepositRequest;

public interface IPaymentService {
    Payment deposit(DepositRequest request);

    String getType();
}
