package com.xuanluan.practice.interviewnonblocking.repository;

import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface IProductRepository extends R2dbcRepository<Product, Long> {
}
