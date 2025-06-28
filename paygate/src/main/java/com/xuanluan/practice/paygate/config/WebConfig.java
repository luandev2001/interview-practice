package com.xuanluan.practice.paygate.config;


import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ConfigurationPropertiesScan(value = "com.xuanluan.practice.paygate.model.property")
@Configuration
public class WebConfig {
    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
