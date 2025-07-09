package com.xuanluan.practice.paygate.model.request.external;

import java.math.BigDecimal;

public record VNPayIpnRequest(String vnp_TmnCode, BigDecimal vnp_Amount, String vnp_BankCode, String vnp_OrderInfo,
                              String vnp_TransactionNo, String vnp_ResponseCode, String vnp_TransactionStatus,
                              String vnp_TxnRef, String vnp_SecureHash) {
}
