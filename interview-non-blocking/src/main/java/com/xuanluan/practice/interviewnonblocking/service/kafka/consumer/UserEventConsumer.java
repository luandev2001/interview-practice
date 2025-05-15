package com.xuanluan.practice.interviewnonblocking.service.kafka.consumer;

import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.service.IUserEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "enable.kafka.consumer", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@Service
public class UserEventConsumer {
    private final IUserEventService userEventService;

    @RetryableTopic
    @KafkaListener(topics = KafkaConstant.Topic.USER_EVENT, groupId = "${spring.application.name}")
    public void consume(UserEventRequest request, Acknowledgment ack) {
        log.info("Received event: {}", request);
        userEventService.create(request).block();
        ack.acknowledge();
    }
}
