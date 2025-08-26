package com.xuanluan.practice.optimise.service.strategy;

import org.simpleflatmapper.csv.CsvParser;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SfmCsvStrategy<T> implements CsvStrategy<T> {
    @Override
    public List<T> loadToObject(File file, Class<T> clazz) throws Exception {
        try (Reader reader = new FileReader(file)) {
            return CsvParser.mapTo(clazz)
                    .stream(reader)
                    .toList();
        }
    }

    @Override
    public List<String[]> loadRaw(File file) throws Exception {
        try (Reader reader = new FileReader(file)) {
            return CsvParser.stream(reader).map(row -> Arrays.copyOf(row, row.length)).toList();
        }
    }

    @Override
    public void export(File file, List<T> data) throws Exception {
        try (Writer writer = new FileWriter(file)) {
            for (T t : data) {
                writer.write(t.toString() + "\n");
            }
        }
    }
}
