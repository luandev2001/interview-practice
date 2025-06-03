package com.xuanluan.practice.interviewnonblocking.service.imp;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(value = "enable.command_runner", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
@Service
public class CommandRunnerImp {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostConstruct
    public void init() {
        pushKafka();
    }

    private void pushKafka() {
        var size = 500;
        for (int i = 0; i < size; i++) {
            kafkaTemplate.send("test-topic", "msg-" + i);
        }
        log.info("Sent {} messages to test-topic", size);
    }
}
