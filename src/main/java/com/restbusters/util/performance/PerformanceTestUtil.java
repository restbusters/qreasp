package com.restbusters.util.performance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.exception.RecordNotFound;
import com.restbusters.http.helper.HttpRequestHelper;
import com.restbusters.http.helper.HttpResultAnalyzer;
import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.http.helper.model.PerfExecResult;
import com.restbusters.rest.model.HttpRequest;
import freemarker.template.TemplateException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTestUtil {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String runPerformanceTest(
            OkHttpClient client,
            HttpRequest request,
            ObjectMapper mapper,
            int threadPoolSizeStart,
            int threadPoolSizeEnd,
            int iterations,
            Long startTimeRange,
            Long endTimeRange
    ) throws InterruptedException, JsonProcessingException, TemplateException, RecordNotFound, IOException {
        List<HttpExecutionResult> allExecutionResults = new ArrayList<>();
        for (int iteration = 0; iteration < iterations; iteration++) {
            int threadPoolSize = (iteration == iterations - 1)
                    ? threadPoolSizeEnd
                    : getThreadCountForIteration(threadPoolSizeStart, threadPoolSizeEnd, iterations, iteration);

            ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
            CountDownLatch latch = new CountDownLatch(threadPoolSize);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);
            // Use thread-safe list for concurrent access
            List<HttpExecutionResult> iterationResults = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < threadPoolSize; i++) {
                executorService.submit(() -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextLong(startTimeRange, endTimeRange));
                        logger.debug("Executing request in thread: {}", Thread.currentThread().getName());
                        HttpExecutionResult result = HttpRequestHelper.executeHttpRequest(client, request);
                        iterationResults.add(result);
                        if (result.isSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        logger.error("Error executing request in thread {}: {}", Thread.currentThread().getName(), e.getMessage());
                        failureCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await(1, TimeUnit.MINUTES);
            executorService.shutdown();
            allExecutionResults.addAll(iterationResults);
            PerfExecResult iterationPerfResult = HttpResultAnalyzer.analyzePerformanceResults(iterationResults);
            logger.info("Iteration {} results: {}", iteration + 1, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(iterationPerfResult));
        }
        PerfExecResult overallPerfResult = HttpResultAnalyzer.analyzePerformanceResults(allExecutionResults);
        String jsonPerfResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(overallPerfResult);
        logger.info("Overall performance results ({} iterations): {}", iterations, jsonPerfResult);
        return jsonPerfResult;
    }

    /**
     * Calculates the thread count for a given iteration, incrementing evenly from start to end over the total number of iterations.
     *
     * @param start           The starting thread count (inclusive).
     * @param end             The ending thread count (inclusive).
     * @param totalIterations The total number of iterations to distribute the thread count across.
     * @param currentIteration The current iteration index (zero-based).
     * @return The thread count to use for the current iteration.
     */
    public static int getThreadCountForIteration(int start, int end, int totalIterations, int currentIteration) {
        if (totalIterations <= 1) return start;
        int step = (end - start) / (totalIterations - 1);
        return start + (step * currentIteration);
    }
}
