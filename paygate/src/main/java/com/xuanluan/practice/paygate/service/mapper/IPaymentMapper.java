package com.xuanluan.practice.paygate.service.mapper;

import com.xuanluan.practice.paygate.model.entity.Payment;
import com.xuanluan.practice.paygate.model.request.TransferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IPaymentMapper {
    @Mapping(target = "currency", source = "toCurrency")
    Payment toPayment(TransferRequest request);
}
