package com.xuanluan.practice.interviewnonblocking.model.request;

import java.time.Instant;
import java.util.UUID;

public record UserEventRequest(UUID userId, String eventId, String eventType, Instant createdAt) {

}
