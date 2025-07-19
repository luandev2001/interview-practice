package com.xuanluan.practice.paygate.service;

import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IPaymentService {
    DepositResponse deposit(DepositRequest request);

    Object ipn(HttpServletRequest request);

    String getMethodCode();
}
