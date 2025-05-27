package com.xuanluan.practice.interviewnonblocking.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.xuanluan.practice.interviewnonblocking.constant.ServiceConstant;
import com.xuanluan.practice.interviewnonblocking.model.property.TTLCacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                          TTLCacheProperties cacheProperties) {

        Map<String, RedisCacheConfiguration> configMap = cacheProperties.getRedis()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> RedisCacheConfiguration.defaultCacheConfig().entryTtl(e.getValue())
                ));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(ServiceConstant.Default.CACHE_EXPIRE_TIME))
                .withInitialCacheConfigurations(configMap)
                .build();
    }

    @Bean
    public CacheManager caffeineCacheManager(TTLCacheProperties cacheProperties) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        Map<String, Duration> caffeineMap = cacheProperties.getCaffeine();

        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfter(new Expiry<>() {
                    @Override
                    public long expireAfterCreate(Object key, Object value, long currentTime) {
                        String cacheKey = String.valueOf(key);
                        Duration ttl = caffeineMap.getOrDefault(cacheKey, ServiceConstant.Default.CACHE_EXPIRE_TIME);
                        return ttl.toNanos();
                    }

                    @Override
                    public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
        );

        return manager;
    }
}
