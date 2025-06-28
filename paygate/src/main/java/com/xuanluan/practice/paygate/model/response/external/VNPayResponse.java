package com.xuanluan.practice.paygate.model.response.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayResponse {
    private String message;
    private String vnpResponseCode;
    private String vnpTransactionStatus;
}
