package com.xuanluan.practice.interviewnonblocking.service.mapper;

import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.request.ProductRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    Product toProduct(ProductRequest productRequest);
}
