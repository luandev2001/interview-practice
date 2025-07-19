package com.xuanluan.practice.paygate.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PaymentConstant {
    public enum Status {
        PENDING, CONFIRMED, CANCELED, NETWORK_CONFIRMED, NETWORK_REVERTED;
    }

    public static class VNPay {
        @Getter
        @RequiredArgsConstructor
        public enum ResponseCode {
            SUCCESS("00"), ANOTHER_ERROR("99");

            private final String code;
        }
    }

    public static class Method {
        public enum Code {
            vnpay
        }
    }
}
