package com.xuanluan.practice.interviewnonblocking.controller;


import com.xuanluan.practice.interviewnonblocking.constant.KafkaConstant;
import com.xuanluan.practice.interviewnonblocking.model.request.UserEventRequest;
import com.xuanluan.practice.interviewnonblocking.model.response.WrapperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/everyone/user_events")
public class UserEventController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Why push data to Kafka?
     * - easy to retry, resilient
     * - avoid bottleneck
     * - optimise save to database
     **/
    @Transactional
    @PostMapping
    public Mono<WrapperResponse<UserEventRequest>> create(@RequestBody Mono<UserEventRequest> requestMono) {
        return requestMono.doOnNext(request -> kafkaTemplate.send(KafkaConstant.Topic.USER_EVENT, request))
                .map(request ->
                        WrapperResponse.<UserEventRequest>builder()
                                .message("Processing!!!")
                                .data(request)
                                .build()
                );
    }
}
