package com.xuanluan.practice.interviewnonblocking.service;

import reactor.core.publisher.Mono;

public interface IUpdateService <T, DTO>{
    Mono<T> update(DTO dto);
}
