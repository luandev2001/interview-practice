package com.xuanluan.practice.interviewnonblocking.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@ConditionalOnProperty(name = "enable.consumer.batch", havingValue = "true")
@Service
public class TestBatchConsumer extends TestConsumer {
    public TestBatchConsumer(ConsumerFactory<String, Object> consumerFactory) {
        super(consumerFactory);
    }

    @KafkaListener(topics = "test-topic", groupId = "test_batch", batch = "true", containerFactory = "batchConsumerFactory")
    public void listenBatch(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        records.forEach(this::logInfoAtOffset);
        ack.acknowledge();
    }
}
