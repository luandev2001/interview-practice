package com.xuanluan.practice.interviewnonblocking.test.api;


import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.repository.IUserEventRepository;
import com.xuanluan.practice.interviewnonblocking.service.mapper.IUserEventMapper;
import com.xuanluan.practice.interviewnonblocking.test.InterviewNonBlockingApplicationTests;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"enable.kafka.consumer=true"})
@EmbeddedKafka(partitions = 1, topics = KafkaConstant.Topic.USER_EVENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserEventApiTest extends InterviewNonBlockingApplicationTests {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private IUserEventRepository userEventRepository;
    @Autowired
    private IUserEventMapper userEventMapper;

    @Test
    void testKafkaUserEvent() {
        UserEventRequest req = new UserEventRequest(UUID.randomUUID(), "payment_1", "payment", "deposit");

        webTestClient.post()
                .uri("/everyone/user_events")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("CREATED!!!");

        // await data to db and check data
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .pollInterval(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    UserEvent userEvent = userEventMapper.toUserEvent(req);
                    Mono<UserEvent> eventMono = userEventRepository.findOne(Example.of(userEvent));
                    UserEvent userEvent1 = eventMono.block();
                    assertNotNull(userEvent1);
                    assertEquals(userEvent1.getEventId(), req.eventId());
                    assertEquals(userEvent1.getUserId(), req.userId());
                    assertEquals(userEvent1.getEventType(), req.eventType());
                    assertEquals(userEvent1.getType(), req.type());
                });
    }
}
