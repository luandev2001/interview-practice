package com.xuanluan.practice.interviewnonblocking.model.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
