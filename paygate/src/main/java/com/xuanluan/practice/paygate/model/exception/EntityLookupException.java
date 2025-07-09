package com.xuanluan.practice.paygate.model.exception;

import jakarta.persistence.EntityNotFoundException;

import java.util.Map;
import java.util.stream.Collectors;

public class EntityLookupException extends EntityNotFoundException {
    public EntityLookupException(String entityName, Map<String, Object> attributes) {
        super(buildMessage(entityName, attributes));
    }

    public EntityLookupException(String entityName, Map<String, Object> attributes, Exception cause) {
        super(buildMessage(entityName, attributes), cause);
    }

    public static EntityLookupException build(Class<?> entityClass, Map<String, Object> attributes) {
        return new EntityLookupException(entityClass.getSimpleName(), attributes);
    }

    public static EntityLookupException build(Class<?> entityClass, Map<String, Object> attributes, Exception cause) {
        return new EntityLookupException(entityClass.getSimpleName(), attributes, cause);
    }

    private static String buildMessage(String entityName, Map<String, Object> attributes) {
        String attrString = attributes.entrySet()
                .stream()
                .map(e -> String.format("%s='%s'", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        return String.format("Entity `%s` with %s does not exist", entityName, attrString);
    }
}
