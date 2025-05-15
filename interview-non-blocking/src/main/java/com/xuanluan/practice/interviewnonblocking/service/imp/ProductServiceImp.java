package com.xuanluan.practice.interviewnonblocking.service.imp;

import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.request.ProductRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IProductRepository;
import com.xuanluan.practice.interviewnonblocking.service.IProductService;
import com.xuanluan.practice.interviewnonblocking.service.mapper.IProductMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceImp implements IProductService {
    private final IProductRepository productRepository;
    private final IProductMapper productMapper;

    @Override
    public Mono<Product> create(ProductRequest productRequest) {
        return productRepository.save(productMapper.toProduct(productRequest));
    }
}
