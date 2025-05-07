package com.xuanluan.practice.interviewnonblocking.service.kafka.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Instant;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class TestConsumer {
    protected final ConsumerFactory<String, Object> consumerFactory;
    private final Instant startTime = Instant.now();

    protected long getLastOffset(String topic, int partition) {
        TopicPartition topicPartition = new TopicPartition(topic, partition);

        try (Consumer<String, ?> consumer = consumerFactory.createConsumer()) {
            consumer.assign(Collections.singletonList(topicPartition));
            consumer.seekToEnd(Collections.singletonList(topicPartition));
            return consumer.position(topicPartition);
        }
    }

    protected void logInfoAtOffset(ConsumerRecord<?, ?> record) {
        long lastOffset = getLastOffset(record.topic(), record.partition());

        log.info("current offset: {}", record.offset());
        log.info("end offset: {}", lastOffset);
        if (record.offset() + 1 == lastOffset) {
            Instant endTime = Instant.now();
            log.info("Consumer Finished: {}", endTime);
            log.info("Depend Timestamp to Consumer Finished: {}", endTime.toEpochMilli() - startTime.toEpochMilli());
        }
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> Start at " + Instant.now());
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("<<< End at " + Instant.now());
    }
}
