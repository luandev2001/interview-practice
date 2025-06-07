package com.xuanluan.practice.interviewnonblocking.service.kafka.producer;

import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.model.entity.BaseEntity;
import com.xuanluan.practice.interviewnonblocking.model.request.ElasticsearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ElasticsearchProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T extends BaseEntity<?>> void toElasticsearch(T entity) {
        kafkaTemplate.send(
                KafkaConstant.Topic.SAVE_ES,
                new ElasticsearchRequest(entity.getClass().getName(), entity.getId().toString())
        );
    }
}
