package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.external.VNPayClient;
import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.property.VNPayProperty;
import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;
import com.xuanluan.practice.paygate.model.response.external.VNPayResponse;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.xuanluan.practice.paygate.model.constant.PaymentConstant.VNPay.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class VNPayServiceImp extends BaseService {
    private final VNPayClient vnPayClient;
    private final VNPayProperty property;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object ipn(HttpServletRequest request) {
        var body = new HashMap<String, String>();
        var charset = StandardCharsets.UTF_8;
        var skipFields = Set.of("vnp_SecureHash", "vnp_SecureHashType");
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode(params.nextElement(), charset);
            if (skipFields.contains(fieldName)) continue;

            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), charset);
            if (StringUtils.hasLength(fieldValue)) body.put(fieldName, fieldValue);
        }

        var signValue = vnPayClient.hashAllFields(body);
        var vnpSecureHash = request.getParameter("vnp_SecureHash");
        if (!Objects.equals(signValue, vnpSecureHash)) {
            return new VNPayResponse("Invalid Checksum", ResponseCode.ANOTHER_ERROR.getCode());
        }

        var paymentId = UUID.fromString(request.getParameter("vnp_TxnRef"));
        var payment = paymentRepository.lockById(paymentId).orElse(null);
        if (payment == null) {
            return new VNPayResponse("Payment Not Found", ResponseCode.ANOTHER_ERROR.getCode());
        }
        if (payment.getStatus() != PaymentConstant.Status.PENDING) {
            Sentry.captureMessage("[Payment][Duplicate IPN] paymentId: " + payment.getId());
            return new VNPayResponse("Duplicate Call IPN", ResponseCode.SUCCESS.getCode());
        }

        var bankCode = request.getParameter("vnp_BankCode");
        var bank = getBank(bankCode);
        payment.setBank(bank);
        payment.setExternalId(request.getParameter("vnp_TransactionNo"));

        if (Objects.equals("00", request.getParameter("vnp_ResponseCode"))) {
            payment.setStatus(PaymentConstant.Status.NETWORK_CONFIRMED);
        } else {
            payment.setStatus(PaymentConstant.Status.NETWORK_REVERTED);
        }
        return new VNPayResponse("Payment Success", ResponseCode.SUCCESS.getCode());
    }

    @Override
    public String getMethodCode() {
        return PaymentConstant.Method.Code.vnpay.name();
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
        Assert.isTrue(
                request.getAmount().longValue() >= property.getMinAmount(),
                String.format("request amount must greater than or equal %d", property.getMinAmount())
        );
    }
}
