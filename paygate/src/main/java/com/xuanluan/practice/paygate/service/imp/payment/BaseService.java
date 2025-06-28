package com.xuanluan.practice.paygate.service.imp.payment;

import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import com.xuanluan.practice.paygate.model.request.DepositRequest;
import com.xuanluan.practice.paygate.model.request.TransferRequest;
import com.xuanluan.practice.paygate.repository.IPaymentMethodRepository;
import com.xuanluan.practice.paygate.repository.IPaymentRepository;
import com.xuanluan.practice.paygate.service.IPaymentService;
import com.xuanluan.practice.paygate.service.mapper.IPaymentMapper;
import org.hibernate.FetchNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

public abstract class BaseService implements IPaymentService {
    @Autowired
    protected IPaymentRepository paymentRepository;
    @Autowired
    protected IPaymentMethodRepository paymentMethodRepository;
    @Autowired
    protected IPaymentMapper paymentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment deposit(DepositRequest request) {
        validateDeposit(request);

        Payment payment = buildPaymentDeposit(request);
        payment = paymentRepository.saveAndFlush(payment);
        handleDeposit(payment);

        return payment;
    }

    protected abstract void handleDeposit(Payment payment);

    protected Payment buildPaymentDeposit(TransferRequest request) {
        Payment payment = paymentMapper.toPayment(request);
        payment.setPaymentMethod(getPaymentMethod(request.getPaymentMethodId()));

        return payment;
    }

    protected void validateDeposit(DepositRequest request) {
        validateTransfer(request);
    }

    protected void validateTransfer(TransferRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(request.getPaymentMethodId(), "payment_method_id can not be null");
        Assert.notNull(request.getUserId(), "user_id can not be null");
        Assert.isTrue(request.getAmount().doubleValue() == 0, "amount must be > 0");
    }

    protected PaymentMethod getPaymentMethod(UUID paymentMethodId) {
        return paymentMethodRepository.findFirstByIdAndCode(paymentMethodId, getType())
                .orElseThrow(() -> new FetchNotFoundException(PaymentMethod.class.getSimpleName(), paymentMethodId));
    }
}
