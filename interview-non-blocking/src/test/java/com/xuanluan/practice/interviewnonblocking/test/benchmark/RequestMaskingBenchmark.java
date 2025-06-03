package com.xuanluan.practice.interviewnonblocking.test.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class RequestMaskingBenchmark {
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile("\"(password|secret|token)\"\\s*:\\s*\"[^\"]*\"");
    private static final Pattern SENSITIVE_AND_SPACE_PATTERN = Pattern.compile("\"(password|secret|token)\"\\s*:\\s*\"[^\"]*\"|\\s+");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("\"password\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"token\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern SECRET_PATTERN = Pattern.compile("\"secret\"\\s*:\\s*\"(.*?)\"");

    public static void main(String[] args) throws Exception {
        int concurrentRequests = 200_000;

        ExecutorService executor = Executors.newFixedThreadPool(8);

        System.out.println("Running benchmark...");

        // ReplaceAll 1
        long regex1Start = System.currentTimeMillis();
        runParallel(executor, concurrentRequests, RequestMaskingBenchmark::maskWithRegex1);
        long regex1End = System.currentTimeMillis();

        // ReplaceAll 2
        long regex2Start = System.currentTimeMillis();
        runParallel(executor, concurrentRequests, RequestMaskingBenchmark::maskWithRegex2);
        long regex2End = System.currentTimeMillis();

        // ReplaceAll 3
        long regex3Start = System.currentTimeMillis();
        runParallel(executor, concurrentRequests, RequestMaskingBenchmark::maskWithRegex3);
        long regex3End = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("Regex1:     " + (regex1End - regex1Start) + " ms");
        System.out.println("Regex2:    " + (regex2End - regex2Start) + " ms");
        System.out.println("Regex3:    " + (regex3End - regex3Start) + " ms");
    }

    private static void runParallel(ExecutorService executor, int count, Runnable task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(i -> executor.submit(() -> {
            task.run();
            latch.countDown();
        }));
        latch.await();
    }

    public static void maskWithRegex1() {
        String result = generateJsonBody();
        result = PASSWORD_PATTERN.matcher(result).replaceAll("\"password\":\"***\"");
        result = TOKEN_PATTERN.matcher(result).replaceAll("\"token\":\"***\"");
        result = SECRET_PATTERN.matcher(result).replaceAll("\"secret\":\"***\"");
        result = result.replaceAll("\\s+", "");
    }

    public static void maskWithRegex2() {
        String result = generateJsonBody();
        result = SENSITIVE_PATTERN.matcher(result).replaceAll("\"$1\":\"***\"");
        result = result.replaceAll("\\s+", "");
    }

    public static void maskWithRegex3() {
        String result = generateJsonBody();
        result = SENSITIVE_AND_SPACE_PATTERN.matcher(result)
                .replaceAll(m ->
                        m.group().startsWith("\"") ? "\"" + m.group(1) + "\":\"***\"" : ""
                );
        result = result.replaceAll("\\s+", "");
    }

    private static String generateJsonBody() {
        return """
                {
                    "userId": "7677f33c-681f-4193-9862-adc43175b445",
                    "eventId": "3477f33c-681f-4193-9862-adc43175b446",
                    "eventType": "Product",
                    "createdAt": "20-03-2025....",
                    "type": "view_product",
                    "password":"mypassword",
                    "token": "abc.def.ghi"
                }
                """;
    }
}
