package com.xuanluan.practice.optimise.script;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvScript {
    public static void generateUser(int size) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("optimise/bigfile.csv"))) {
            writer.write("id,name,email,age");
            writer.newLine();

            for (long i = 1; i <= size; i++) {
                writer.write(i + ",User" + i + ",user" + i + "@example.com," + ((i % 100) + 18));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done Generate File CSV size: "+ size);
    }
}
