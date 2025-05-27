package com.xuanluan.practice.interviewnonblocking.service;

import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.request.ProductRequest;
import reactor.core.publisher.Mono;

public interface IProductService extends ICreateService<Product, ProductRequest> {
    Mono<Product> getById(long id);
}
