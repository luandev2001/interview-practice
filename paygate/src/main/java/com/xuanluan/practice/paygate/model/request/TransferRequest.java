package com.xuanluan.practice.paygate.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransferRequest {
    private String paymentMethodCode;
    private BigDecimal amount;
    private UUID userId;
    private String description;
}
