package com.xuanluan.practice.interviewnonblocking.constant;

import java.time.Duration;

public class ServiceConstant {
    public static class Default {
        public static final Duration CACHE_EXPIRE_TIME = Duration.ofMinutes(5);
    }

    public static class OutboxType {
        public static final String CREATE = "create";
    }
}
