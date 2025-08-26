package com.xuanluan.practice.optimise.service.strategy;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.List;

public class OpenCsvStrategy<T> implements CsvStrategy<T> {
    @Override
    public List<T> loadToObject(File file, Class<T> clazz) throws Exception {
        try (Reader reader = new FileReader(file)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }

    @Override
    public List<String[]> loadRaw(File file) throws Exception {
        return List.of();
    }

    @Override
    public void export(File file, List<T> data) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            for (T t : data) {
                writer.writeNext(new String[]{t.toString()});
            }
        }
    }
}
