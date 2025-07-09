package com.xuanluan.practice.paygate.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PaymentConstant {
    public enum Status {
        PENDING, CONFIRMED, CANCELED, NETWORK_CONFIRMED;
    }

    public static class VNPay {
        @Getter
        @RequiredArgsConstructor
        public enum ResponseCode {
            SUCCESS("00"), NOT_FOUND("01"), CONFIRMED("02"), ANOTHER_ERROR("99");

            private final String code;
        }
    }
}
