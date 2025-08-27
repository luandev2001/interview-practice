package com.xuanluan.practice.optimise.benchmark;

import com.xuanluan.practice.optimise.model.parser.UserCsvParser;
import com.xuanluan.practice.optimise.service.strategy.CsvStrategy;
import com.xuanluan.practice.optimise.service.strategy.UnivocityCsvStrategy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class LibraryCsvBenchmark {
    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LibraryCsvBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(5)
                .forks(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .resultFormat(ResultFormatType.JSON)
                .result("/dev/null")
                .shouldFailOnError(true)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }

    private CsvStrategy<UserCsvParser> strategy;
    private File file;
    private Class<UserCsvParser> typeClass;

    @Setup(Level.Trial)
    public void setup() {
        this.strategy = new UnivocityCsvStrategy<>();
        this.file = new File("optimise/bigfile.csv");
        this.typeClass = UserCsvParser.class;
    }

    @Benchmark
    public void loadToObject() throws Exception {
        strategy.loadToObject(file, typeClass);
    }

    @Benchmark
    public void loadRaw() throws Exception {
        strategy.loadRaw(file);
    }

    @Benchmark
    public void export() throws Exception {
        strategy.export(file, strategy.loadToObject(file, typeClass));
    }
}
