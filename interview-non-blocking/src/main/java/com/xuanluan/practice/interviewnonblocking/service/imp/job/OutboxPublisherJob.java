package com.xuanluan.practice.interviewnonblocking.service.imp.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanluan.practice.interviewnonblocking.repository.IOutboxEventRepository;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class OutboxPublisherJob implements Job {
    private final IOutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) {
        outboxEventRepository.findTop100ByProcessedAtIsNullAndRetriesLessThanOrderByUpdatedAtAsc(3)
                .flatMap(event ->
                        Mono.fromFuture(() -> kafkaTemplate.send(event.getType(), convertJsonToObject(event.getPayload(), getClass(event.getClassPackage()))))
                                .doOnSuccess(_ -> event.setRetries(event.getRetries() + 1))
                                .thenReturn(event)
                )
                .collectList()
                .flatMapMany(outboxEventRepository::saveAll)
                .subscribe();
    }

    private <T> T convertJsonToObject(Json json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json.asString(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid data", e);
        }
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Not found class: " + className, e);
        }
    }
}
