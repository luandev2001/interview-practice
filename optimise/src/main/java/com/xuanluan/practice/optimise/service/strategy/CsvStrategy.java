package com.xuanluan.practice.optimise.service.strategy;

import java.io.File;
import java.util.List;

public interface CsvStrategy<T> {
    List<T> loadToObject(File file, Class<T> clazz) throws Exception;

    List<String[]> loadRaw(File file) throws Exception;

    void export(File file, List<T> data) throws Exception;
}
