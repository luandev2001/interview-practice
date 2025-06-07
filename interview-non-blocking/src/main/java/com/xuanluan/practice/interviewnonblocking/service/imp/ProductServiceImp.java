package com.xuanluan.practice.interviewnonblocking.service.imp;

import com.xuanluan.practice.interviewnonblocking.constant.ServiceConstant;
import com.xuanluan.practice.interviewnonblocking.model.entity.OutboxEvent;
import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.exception.BadRequestException;
import com.xuanluan.practice.interviewnonblocking.model.request.ProductRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IOutboxEventRepository;
import com.xuanluan.practice.interviewnonblocking.repository.IProductRepository;
import com.xuanluan.practice.interviewnonblocking.service.IProductService;
import com.xuanluan.practice.interviewnonblocking.service.kafka.producer.ElasticsearchProducer;
import com.xuanluan.practice.interviewnonblocking.service.mapper.IOutboxEventMapper;
import com.xuanluan.practice.interviewnonblocking.service.mapper.IProductMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServiceImp implements IProductService {
    private final IProductRepository productRepository;
    private final IProductMapper productMapper;
    private final IOutboxEventRepository outboxEventRepository;
    private final IOutboxEventMapper outboxEventMapper;
    private final TransactionalOperator transactionalOperator;
    private final ElasticsearchProducer elasticsearchProducer;

    @Override
    public Mono<Product> create(ProductRequest productRequest) {
        return Mono.just(productRequest)
                .flatMap(this::validateCreate)
                .then(Mono.defer(() ->
                        productRepository.save(productMapper.toProduct(productRequest))
                                .flatMap(product -> {
                                    OutboxEvent outboxEvent = outboxEventMapper.toOutboxEvent(product);
                                    outboxEvent.setType(ServiceConstant.OutboxType.CREATE);
                                    return outboxEventRepository.save(outboxEvent).thenReturn(product);
                                })))
                .as(transactionalOperator::transactional)
                .map(product -> {
                    elasticsearchProducer.toElasticsearch(product);
                    return product;
                });
    }

    private Mono<Void> validateCreate(ProductRequest productRequest) {
        if (productRequest == null) {
            return Mono.error(new BadRequestException("Product is empty"));
        }
        if (!StringUtils.hasText(productRequest.name())) {
            return Mono.error(new BadRequestException("Product name is empty"));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Product> getById(long id) {
        return productRepository.findById(id);
    }
}
