package com.xuanluan.practice.interviewnonblocking.service;

import reactor.core.publisher.Mono;

public interface ICreateService<T, DTO> {
    Mono<T> create(DTO dto);
}
