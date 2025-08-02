package com.xuanluan.practice.paygate.config;

import com.xuanluan.practice.paygate.model.exception.BadRequestException;
import com.xuanluan.practice.paygate.model.response.external.WrapperResponse;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<WrapperResponse<?>> handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponse(exception.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<WrapperResponse<?>> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WrapperResponse<?>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildResponse(exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<WrapperResponse<?>> handleOtherException(Throwable exception, HttpServletRequest request) {
        Sentry.captureException(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildResponse(exception.getMessage()));
    }

    private WrapperResponse<?> buildResponse(String message) {
        return WrapperResponse.builder().message(message).build();
    }
}
