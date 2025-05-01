package com.xuanluan.practice.interviewnonblocking.controller;

import com.xuanluan.practice.interviewnonblocking.model.response.WrapperResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(KafkaException.class)
    public Mono<ResponseEntity<WrapperResponse<?>>> handleKafkaException(KafkaException e) {
        log.error(e.getMessage(), e);
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                WrapperResponse.builder()
                                        .message("Lỗi kafka")
                                        .build()
                        )
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<WrapperResponse<?>>> handleGeneral(Exception e) {
        log.error(e.getMessage(), e);
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                WrapperResponse.builder()
                                        .message("Lỗi hệ thống")
                                        .build()
                        )
        );
    }
}
