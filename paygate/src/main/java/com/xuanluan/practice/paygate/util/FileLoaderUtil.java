package com.xuanluan.practice.paygate.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

public class FileLoaderUtil {
    public static <T> List<T> loadFromJson(String filePath, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        return loadFromJson(filePath, tClass, objectMapper);
    }

    public static <T> List<T> loadFromJson(String filePath, Class<T> tClass, ObjectMapper objectMapper) {
        return loadResource(filePath, is -> {
            try {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, tClass);
                return objectMapper.readValue(is, type);
            } catch (IOException e) {
                throw new RuntimeException("Failed convert to value");
            }
        });
    }

    public static <T> List<T> loadFromCsv(String filePath, Class<T> tClass) {
        return loadResource(filePath, is -> {
            CsvParserSettings settings = new CsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            settings.setIgnoreLeadingWhitespaces(true);
            settings.setIgnoreTrailingWhitespaces(true);
            BeanListProcessor<T> rowProcessor = new BeanListProcessor<>(tClass);
            settings.setProcessor(rowProcessor);
            CsvParser parser = new CsvParser(settings);
            parser.parse(is);

            return rowProcessor.getBeans();
        });
    }

    public static <T> T loadResource(String filePath, Function<InputStream, T> handler) {
        try (InputStream is = new ClassPathResource(filePath).getInputStream()) {
            return handler.apply(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + filePath, e);
        }
    }
}
