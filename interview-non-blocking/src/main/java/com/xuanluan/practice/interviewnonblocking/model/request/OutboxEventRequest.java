package com.xuanluan.practice.interviewnonblocking.model.request;

public record OutboxEventRequest(String type, String eventType, String eventId) {

}
