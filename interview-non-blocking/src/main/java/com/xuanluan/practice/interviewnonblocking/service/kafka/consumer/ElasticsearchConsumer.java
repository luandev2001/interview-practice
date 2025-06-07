package com.xuanluan.practice.interviewnonblocking.service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.constant.ServiceConstant;
import com.xuanluan.practice.interviewnonblocking.model.document.BaseDocument;
import com.xuanluan.practice.interviewnonblocking.model.request.ElasticsearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(name = "enable.kafka.consumer", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticsearchConsumer {
    private final R2dbcEntityOperations entityOperations;
    private final ReactiveElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaConstant.Topic.SAVE_ES)
    public void save(ElasticsearchRequest request, Acknowledgment ack) {
        getClass(request.objectType()).flatMap(entityClass -> {
                    Class<? extends BaseDocument> documentClass = ServiceConstant.ElasticSearch.MAPPINGS.get(entityClass.getSimpleName());
                    return getById(request.objectId(), getColumns(documentClass), entityClass).flatMap(record -> {
                        Object document = objectMapper.convertValue(record, documentClass);
                        return elasticsearchOperations.save(document);
                    });
                })
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .doOnError(e -> log.error("Failed to process ElasticsearchRequest: {}", request, e))
                .doFinally(signal -> {
                    ack.acknowledge();
                    log.info("Finished processing with signal: {}", signal);
                })
                .subscribe();
    }

    private <T> Mono<T> getById(Object id, List<String> columns, Class<T> entityClass) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = Query.query(criteria).columns(columns);
        return entityOperations.selectOne(query, entityClass);
    }

    private Mono<Class<?>> getClass(String className) {
        try {
            return Mono.just(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Mono.error(new IllegalArgumentException("Not found class: " + className, e));
        }
    }

    private List<String> getColumns(Class<?> documentClass) {
        Field[] declaredFields = documentClass.getDeclaredFields();
        Field[] parentFields = documentClass.getSuperclass().getDeclaredFields();
        List<String> columns = new ArrayList<>(declaredFields.length + parentFields.length);

        for (Field field : declaredFields) columns.add(field.getName());
        for (Field field : parentFields) columns.add(field.getName());

        return columns;
    }
}
