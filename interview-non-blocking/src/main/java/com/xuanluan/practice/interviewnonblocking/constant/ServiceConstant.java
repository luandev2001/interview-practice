package com.xuanluan.practice.interviewnonblocking.constant;

import com.xuanluan.practice.interviewnonblocking.model.document.BaseDocument;
import com.xuanluan.practice.interviewnonblocking.model.document.ProductDocument;
import com.xuanluan.practice.interviewnonblocking.model.document.UserEventDocument;
import com.xuanluan.practice.interviewnonblocking.model.entity.Product;
import com.xuanluan.practice.interviewnonblocking.model.entity.UserEvent;

import java.time.Duration;
import java.util.Map;

public class ServiceConstant {
    public static class Default {
        public static final Duration CACHE_EXPIRE_TIME = Duration.ofMinutes(5);
    }

    public static class ElasticSearch {
        public static final Map<String, Class<? extends BaseDocument>> MAPPINGS = Map.of(
                Product.class.getSimpleName(), ProductDocument.class,
                UserEvent.class.getSimpleName(), UserEventDocument.class
        );
    }
}
