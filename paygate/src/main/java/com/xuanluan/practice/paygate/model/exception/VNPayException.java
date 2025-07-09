package com.xuanluan.practice.paygate.model.exception;

import lombok.Getter;

@Getter
public class VNPayException extends RuntimeException {
    private final String code;

    public VNPayException(String code, String message) {
        super(message);
        this.code = code;
    }
}
