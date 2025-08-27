package com.xuanluan.practice.optimise.service.strategy;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

public class UnivocityCsvStrategy<T> implements CsvStrategy<T> {
    @Override
    public List<T> loadToObject(File file, Class<T> clazz) throws Exception {
        BeanListProcessor<T> rowProcessor = new BeanListProcessor<>(clazz);
        CsvParserSettings settings = new CsvParserSettings();
        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(settings);
        parser.parse(file);
        return rowProcessor.getBeans();
    }

    @Override
    public List<String[]> loadRaw(File file) throws Exception {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(false);
        CsvParser parser = new CsvParser(settings);
        parser.parse(file);
        return parser.parseAll();
    }

    @Override
    public void export(File file, List<T> data) throws Exception {
        CsvWriterSettings settings = new CsvWriterSettings();
        try (Writer writer = new FileWriter(file)) {
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for (T t : data) {
                csvWriter.writeRow(t.toString());
            }
            csvWriter.flush();
        }
    }
}
