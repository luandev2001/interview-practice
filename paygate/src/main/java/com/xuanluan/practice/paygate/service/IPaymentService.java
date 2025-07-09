package com.xuanluan.practice.paygate.service;

import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;

public interface IPaymentService {
    DepositResponse deposit(DepositRequest request);

    String getMethodCode();
}
