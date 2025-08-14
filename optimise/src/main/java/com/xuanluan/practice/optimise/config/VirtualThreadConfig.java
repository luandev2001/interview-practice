//package com.xuanluan.practice.optimise.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
//@Slf4j
//@Configuration
//@EnableAsync
//public class VirtualThreadConfig {
//
//    @Bean
//    @ConditionalOnProperty(name = "server.threads.virtual.enabled", havingValue = "true")
//    public Executor virtualThreadExecutor() {
//        log.info("Configuring Virtual Thread Executor");
//
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(200);
//        executor.setMaxPoolSize(1000);
//        executor.setQueueCapacity(1000);
//        executor.setKeepAliveSeconds(60);
//        executor.setThreadNamePrefix("virtual-");
//        executor.setTaskDecorator(runnable -> {
//            log.debug("Executing task on virtual thread: {}", Thread.currentThread().getName());
//            return runnable;
//        });
//        executor.initialize();
//
//        return executor;
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "server.threads.virtual.enabled", havingValue = "false")
//    public Executor platformThreadExecutor() {
//        log.info("Configuring Platform Thread Executor");
//
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(50);
//        executor.setQueueCapacity(100);
//        executor.setKeepAliveSeconds(60);
//        executor.setThreadNamePrefix("platform-");
//        executor.setTaskDecorator(runnable -> {
//            log.debug("Executing task on platform thread: {}", Thread.currentThread().getName());
//            return runnable;
//        });
//        executor.initialize();
//
//        return executor;
//    }
//}