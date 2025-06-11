package com.xuanluan.practice.interviewnonblocking.config;

import com.xuanluan.practice.interviewnonblocking.model.document.BaseDocument;
import com.xuanluan.practice.interviewnonblocking.model.document.ProductDocument;
import com.xuanluan.practice.interviewnonblocking.model.document.UserEventDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveIndexOperations;
import reactor.core.publisher.Flux;

import java.util.List;

@ConditionalOnProperty(name = "elasticsearch.migrate", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ElasticsearchConfig {
    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        List<Class<? extends BaseDocument>> documentClasses = List.of(
                ProductDocument.class,
                UserEventDocument.class
        );

        Flux.fromIterable(documentClasses)
                .flatMap(docClass -> {
                    ReactiveIndexOperations indexOperations = reactiveElasticsearchOperations.indexOps(docClass);
                    String indexName = indexOperations.getIndexCoordinates().getIndexName();

                    return indexOperations.exists()
                            .flatMap(exists -> {
                                if (exists) {
                                    log.info("Index already exists: {}", indexName);
                                    return indexOperations.putMapping();
                                } else {
                                    return indexOperations.create()
                                            .doOnSuccess(_ -> log.info("Index created: {}", indexName))
                                            .then(indexOperations.putMapping())
                                            .doOnSuccess(_ -> log.info("Mapping created for: {}", indexName));
                                }
                            });
                })
                .doOnError(error -> log.error("Failed to create index", error))
                .subscribe();
    }
}
