package com.xuanluan.practice.paygate.controller;


import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import com.xuanluan.practice.paygate.model.response.external.VNPayResponse;
import com.xuanluan.practice.paygate.service.IPaymentService;
import com.xuanluan.practice.paygate.service.imp.payment.PaymentFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xuanluan.practice.paygate.model.constant.PaymentConstant.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("payment/ipns")
public class PaymentIpnController {
    private final PaymentFactory paymentFactory;

    @GetMapping("vnpay")
    public Object vnpay(HttpServletRequest request) {
        try {
            IPaymentService paymentService = paymentFactory.get(PaymentConstant.Method.Code.vnpay.name());
            return paymentService.ipn(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new VNPayResponse("Internal Error", VNPay.ResponseCode.ANOTHER_ERROR.getCode());
        }
    }
}
