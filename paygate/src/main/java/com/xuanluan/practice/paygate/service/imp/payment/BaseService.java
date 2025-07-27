package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.model.constant.PaymentConstant;
import com.xuanluan.practice.paygate.model.entity.Bank;
import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.model.exception.EntityLookupException;
import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.request.TransferRequest;
import com.xuanluan.practice.paygate.model.response.DepositResponse;
import com.xuanluan.practice.paygate.repository.IBankRepository;
import com.xuanluan.practice.paygate.repository.IPaymentMethodRepository;
import com.xuanluan.practice.paygate.repository.IPaymentRepository;
import com.xuanluan.practice.paygate.repository.scope.BankSpec;
import com.xuanluan.practice.paygate.repository.scope.PaymentMethodSpec;
import com.xuanluan.practice.paygate.service.IPaymentService;
import com.xuanluan.practice.paygate.service.mapper.IPaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class BaseService implements IPaymentService {
    @Autowired
    protected IPaymentRepository paymentRepository;
    @Autowired
    protected IPaymentMethodRepository paymentMethodRepository;
    @Autowired
    protected IBankRepository bankRepository;
    @Autowired
    protected IPaymentMapper paymentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DepositResponse deposit(DepositRequest request) {
        validateDeposit(request);

        Payment payment = buildDeposit(request);
        payment = paymentRepository.saveAndFlush(payment);

        return handleDeposit(payment, request.getMetadata());
    }

    protected abstract DepositResponse handleDeposit(Payment payment, Map<String, Object> metaData);

    protected Payment buildDeposit(TransferRequest request) {
        var payment = paymentMapper.toPayment(request);
        var paymentMethod = getPaymentMethod(request.getPaymentMethodCode());
        payment.setPaymentMethod(paymentMethod);
        // fee = (amount * deposit_fee_percent / 100 ) + deposit_fee
        var fee = request.getAmount()
                .multiply(paymentMethod.getDepositFeePercent())
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
                .add(paymentMethod.getDepositFee())
                .setScale(PaymentConstant.Round.DEFAULT, RoundingMode.DOWN);
        payment.setFee(fee);
        payment.setReceivedAmount(request.getAmount().subtract(fee));
        return payment;
    }

    protected void validateDeposit(DepositRequest request) {
        validateTransfer(request);
    }

    protected void validateTransfer(TransferRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(request.getPaymentMethodCode(), "payment_method_code can not be null");
        Assert.notNull(request.getUserId(), "user_id can not be null");
        Assert.isTrue(request.getAmount() != null && request.getAmount().doubleValue() > 0, "amount must be > 0");
        Assert.isTrue(StringUtils.hasLength(request.getDescription()), "description can not be blank");
    }

    protected PaymentMethod getPaymentMethod(String code) {
        PaymentMethod paymentMethod = paymentMethodRepository.findOne(PaymentMethodSpec.activeWithCode(List.of(code)))
                .orElseThrow(() -> EntityLookupException.build(PaymentMethod.class, Map.of("code", code)));
        Assert.isTrue(Objects.equals(paymentMethod.getCode().name(), getMethodCode()), "payment_method is not match");

        return paymentMethod;
    }

    protected Bank getBank(String code) {
        return StringUtils.hasLength(code) ? bankRepository.findOne(BankSpec.activeWithCode(List.of(code))).orElse(null) : null;
    }
}
