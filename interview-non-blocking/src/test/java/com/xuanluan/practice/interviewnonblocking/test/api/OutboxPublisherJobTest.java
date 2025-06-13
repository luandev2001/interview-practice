package com.xuanluan.practice.interviewnonblocking.test.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanluan.practice.interviewnonblocking.model.entity.OutboxEvent;
import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.request.JobClockworkRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IOutboxEventRepository;
import com.xuanluan.practice.interviewnonblocking.service.imp.job.OutboxPublisherJob;
import com.xuanluan.practice.interviewnonblocking.test.InterviewNonBlockingApplicationTests;
import io.r2dbc.postgresql.codec.Json;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.*;
import org.quartz.utils.Key;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OutboxPublisherJobTest extends InterviewNonBlockingApplicationTests {
    @MockitoBean
    KafkaTemplate<String, Object> kafkaTemplate;
    @MockitoBean
    IOutboxEventRepository outboxEventRepository;
    @MockitoSpyBean
    ObjectMapper objectMapper;

    @MockitoSpyBean
    Scheduler scheduler;

    @Test
    void should_send_event_to_kafka_and_increment_retry() throws Exception {
        Product product = new Product();
        product.setId(123L);
        product.setName("test");

        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setType("test-topic");
        event.setPayload(Json.of(objectMapper.writeValueAsString(product)));
        event.setClassPackage(product.getClass().getName());

        Product productFromJson = convertJsonToObject(event.getPayload(), product.getClass());
        assertEquals(product.getId(), productFromJson.getId());
        assertEquals(product.getName(), productFromJson.getName());

        when(outboxEventRepository.findTop100ByProcessedAtIsNullAndRetriesLessThanOrderByUpdatedAtAsc(3))
                .thenReturn(Flux.just(event));

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(
                eq(event.getType()),
                argThat(obj ->
                        obj instanceof Product &&
                                ((Product) obj).getId().equals(product.getId()) &&
                                ((Product) obj).getName().equals(product.getName())
                )))
                .thenReturn(CompletableFuture.completedFuture(sendResult));
        when(outboxEventRepository.saveAll(anyList()))
                .thenReturn(Flux.fromIterable(List.of(event)));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        JobClockworkRequest jobClockworkRequest = new JobClockworkRequest(
                buildJob(OutboxPublisherJob.class).build(),
                TriggerBuilder.newTrigger().startNow().build()
        );
        scheduler.scheduleJob(jobClockworkRequest.jobDetail(), jobClockworkRequest.trigger());

        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    verify(kafkaTemplate).send(eq(event.getType()), productArgumentCaptor.capture());
                    Product productOfJob = productArgumentCaptor.getValue();
                    assertThat(productOfJob).isInstanceOf(Product.class);
                    assertEquals(productOfJob.getId(), product.getId());
                    assertEquals(productOfJob.getName(), product.getName());
                });
    }

    private <T> T convertJsonToObject(Json json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json.asString(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid data", e);
        }
    }

    private JobBuilder buildJob(Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(Key.createUniqueName(null), jobClass.getSimpleName())
                .storeDurably()
                .requestRecovery();
    }
}
