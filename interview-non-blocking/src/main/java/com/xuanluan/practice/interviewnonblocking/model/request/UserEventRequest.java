package com.xuanluan.practice.interviewnonblocking.model.request;

import java.util.UUID;

public record UserEventRequest(UUID userId, String eventId, String eventType, String type) {

}
