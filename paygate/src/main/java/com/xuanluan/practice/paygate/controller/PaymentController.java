package com.xuanluan.practice.paygate.controller;

import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;
import com.xuanluan.practice.paygate.model.response.external.WrapperResponse;
import com.xuanluan.practice.paygate.service.IPaymentService;
import com.xuanluan.practice.paygate.service.imp.payment.PaymentFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("payments")
public class PaymentController {
    private final PaymentFactory paymentFactory;

    @PostMapping("deposit")
    public WrapperResponse<DepositResponse> deposit(@RequestBody DepositRequest request, HttpServletRequest servletRequest) {
        Assert.notNull(request.getMetadata(), "metadata must be not null");

        request.getMetadata().put("ipAddress", getIpAddress(servletRequest));
        IPaymentService paymentService = paymentFactory.get(request.getPaymentMethodCode());
        return WrapperResponse.<DepositResponse>builder()
                .data(paymentService.deposit(request))
                .build();
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return StringUtils.hasLength(ip) ? ip : request.getRemoteAddr();
    }
}
