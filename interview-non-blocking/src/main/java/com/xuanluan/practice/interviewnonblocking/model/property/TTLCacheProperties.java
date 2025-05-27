package com.xuanluan.practice.interviewnonblocking.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache.ttl")
public class TTLCacheProperties {
    private Map<String, Duration> redis = new HashMap<>();
    private Map<String, Duration> caffeine = new HashMap<>();
}
