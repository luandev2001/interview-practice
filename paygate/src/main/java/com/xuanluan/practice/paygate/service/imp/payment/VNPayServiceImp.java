package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.external.VNPayClient;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.exception.VNPayException;
import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.request.external.VNPayIpnRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;
import com.xuanluan.practice.paygate.model.response.external.VNPayResponse;
import com.xuanluan.practice.paygate.service.IIpnService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

import static com.xuanluan.practice.paygate.model.constant.PaymentConstant.VNPay.*;

@RequiredArgsConstructor
@Service
public class VNPayServiceImp extends BaseService implements IIpnService<VNPayIpnRequest, VNPayResponse> {
    private final VNPayClient vnPayClient;
    private final EntityManager entityManager;

    @Override
    public VNPayResponse ipn(VNPayIpnRequest request) {
        UUID vnpTxnRef = UUID.fromString(request.vnp_TxnRef());
        Payment payment = entityManager.find(Payment.class, vnpTxnRef, LockModeType.PESSIMISTIC_WRITE);
        if (payment == null) {
            throw new VNPayException("Payment not found", ResponseCode.NOT_FOUND.getCode());
        }
        return new VNPayResponse("Payment success", ResponseCode.SUCCESS.getCode());
    }

    @Override
    public String getMethodCode() {
        return "vnpay";
    }

    @Override
    protected DepositResponse handleDeposit(Payment payment, Map<String, Object> metaData) {
        String url = vnPayClient.createUrl(payment, metaData);

        return new DepositResponse(payment.getId(), url);
    }

    @Override
    protected void validateDeposit(DepositRequest request) {
        super.validateDeposit(request);
        Assert.hasText(request.getMetadata().get("ipAddress").toString(), "ip_address must be not blank");
    }
}
