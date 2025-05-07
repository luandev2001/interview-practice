package com.xuanluan.practice.interviewnonblocking.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "enable.consumer.single", havingValue = "true")
@Service
public class TestSingleConsumer extends TestConsumer {
    public TestSingleConsumer(ConsumerFactory<String, Object> consumerFactory) {
        super(consumerFactory);
    }

    @KafkaListener(topics = "test-topic", groupId = "test_single", containerFactory = "kafkaListenerContainerFactory")
    public void listenSingle(ConsumerRecord<String, String> record, Acknowledgment ack) {
        logInfoAtOffset(record);
        ack.acknowledge();
    }
}
