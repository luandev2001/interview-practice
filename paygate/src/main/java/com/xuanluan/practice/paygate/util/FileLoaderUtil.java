package com.xuanluan.practice.paygate.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanluan.practice.paygate.model.entity.PaymentMethod;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileLoaderUtil {
    public static List<PaymentMethod> loadFromJson(String filePath, ObjectMapper objectMapper) {
        try (InputStream is = new ClassPathResource(filePath).getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load PaymentMethods from file: " + filePath, e);
        }
    }
}
