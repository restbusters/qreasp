package com.restbusters.http.helper;

import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.http.helper.model.PerfExecResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for HttpResultAnalyzer - performance metrics calculation.
 */
public class HttpResultAnalyzerTest {

    // ==================== Percentile Calculation Tests ====================

    @Test(description = "Calculate P50 for sorted list")
    public void testCalculatePercentile_P50() {
        List<Long> times = Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L);
        Long p50 = HttpResultAnalyzer.calculatePercentile(times, 50);
        Assert.assertEquals(p50.longValue(), 55L, "P50 should be median (interpolated)");
    }

    @Test(description = "Calculate P95 for sorted list")
    public void testCalculatePercentile_P95() {
        List<Long> times = Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L);
        Long p95 = HttpResultAnalyzer.calculatePercentile(times, 95);
        // Index = 0.95 * 9 = 8.55, interpolate between index 8 (90) and 9 (100)
        // 90 + 0.55 * (100 - 90) = 90 + 5.5 = 95.5 -> rounded to 95 (Math.round rounds 95.5 to 95 due to banker's rounding)
        Assert.assertTrue(p95 >= 95L && p95 <= 96L, "P95 should be around 95-96, got: " + p95);
    }

    @Test(description = "Calculate P99 for sorted list")
    public void testCalculatePercentile_P99() {
        List<Long> times = Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L);
        Long p99 = HttpResultAnalyzer.calculatePercentile(times, 99);
        // Index = 0.99 * 9 = 8.91, interpolate between index 8 (90) and 9 (100)
        // 90 + 0.91 * (100 - 90) = 90 + 9.1 = 99.1 -> rounded to 99
        Assert.assertEquals(p99.longValue(), 99L, "P99 should be 99");
    }

    @Test(description = "Calculate percentile for empty list returns 0")
    public void testCalculatePercentile_EmptyList() {
        List<Long> times = Collections.emptyList();
        Long p50 = HttpResultAnalyzer.calculatePercentile(times, 50);
        Assert.assertEquals(p50.longValue(), 0L, "Empty list should return 0");
    }

    @Test(description = "Calculate percentile for null list returns 0")
    public void testCalculatePercentile_NullList() {
        Long p50 = HttpResultAnalyzer.calculatePercentile(null, 50);
        Assert.assertEquals(p50.longValue(), 0L, "Null list should return 0");
    }

    @Test(description = "Calculate percentile for single element list")
    public void testCalculatePercentile_SingleElement() {
        List<Long> times = Collections.singletonList(42L);
        Long p50 = HttpResultAnalyzer.calculatePercentile(times, 50);
        Long p99 = HttpResultAnalyzer.calculatePercentile(times, 99);
        Assert.assertEquals(p50.longValue(), 42L, "Single element P50 should be that element");
        Assert.assertEquals(p99.longValue(), 42L, "Single element P99 should be that element");
    }

    @Test(description = "Calculate P0 returns first element")
    public void testCalculatePercentile_P0() {
        List<Long> times = Arrays.asList(10L, 50L, 100L);
        Long p0 = HttpResultAnalyzer.calculatePercentile(times, 0);
        Assert.assertEquals(p0.longValue(), 10L, "P0 should be minimum");
    }

    @Test(description = "Calculate P100 returns last element")
    public void testCalculatePercentile_P100() {
        List<Long> times = Arrays.asList(10L, 50L, 100L);
        Long p100 = HttpResultAnalyzer.calculatePercentile(times, 100);
        Assert.assertEquals(p100.longValue(), 100L, "P100 should be maximum");
    }

    @Test(description = "Calculate percentile with two elements")
    public void testCalculatePercentile_TwoElements() {
        List<Long> times = Arrays.asList(100L, 200L);
        Long p50 = HttpResultAnalyzer.calculatePercentile(times, 50);
        // Index = 0.5 * 1 = 0.5, interpolate between index 0 (100) and 1 (200)
        // 100 + 0.5 * 100 = 150
        Assert.assertEquals(p50.longValue(), 150L, "P50 of two elements should be midpoint");
    }

    // ==================== Analyze Performance Results Tests ====================

    @Test(description = "Analyze results with all successful requests")
    public void testAnalyzePerformanceResults_AllSuccess() {
        List<HttpExecutionResult> results = createResults(
                new long[]{100, 200, 300, 400, 500},
                new int[]{200, 200, 200, 200, 200},
                new ExecutionState[]{ExecutionState.COMPLETED, ExecutionState.COMPLETED,
                        ExecutionState.COMPLETED, ExecutionState.COMPLETED, ExecutionState.COMPLETED}
        );

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getTotalRequests(), 5);
        Assert.assertEquals(perfResult.getSuccessRequests(), 5);
        Assert.assertEquals(perfResult.getFailedRequests(), 0);
        Assert.assertEquals(perfResult.getSuccessRate(), 100.0);
        Assert.assertEquals(perfResult.getMinExecutionTime().longValue(), 100L);
        Assert.assertEquals(perfResult.getMaxExecutionTime().longValue(), 500L);
        Assert.assertEquals(perfResult.getAverageTime(), 300.0);
        Assert.assertNotNull(perfResult.getP50());
        Assert.assertNotNull(perfResult.getP95());
        Assert.assertNotNull(perfResult.getP99());
    }

    @Test(description = "Analyze results with mixed success and failure")
    public void testAnalyzePerformanceResults_MixedResults() {
        List<HttpExecutionResult> results = createResults(
                new long[]{100, 200, 300, 400},
                new int[]{200, 500, 200, 404},
                new ExecutionState[]{ExecutionState.COMPLETED, ExecutionState.COMPLETED,
                        ExecutionState.COMPLETED, ExecutionState.COMPLETED}
        );

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getTotalRequests(), 4);
        Assert.assertEquals(perfResult.getSuccessRequests(), 2);
        Assert.assertEquals(perfResult.getFailedRequests(), 2);
        Assert.assertEquals(perfResult.getSuccessRate(), 50.0);
    }

    @Test(description = "Analyze results with skipped requests")
    public void testAnalyzePerformanceResults_SkippedRequests() {
        List<HttpExecutionResult> results = new ArrayList<>();

        HttpExecutionResult skipped = new HttpExecutionResult();
        skipped.setStatus(ExecutionState.SKIPPED);
        skipped.setExecutionTime(0L);
        skipped.setError("Connection refused");
        results.add(skipped);

        HttpExecutionResult success = new HttpExecutionResult();
        success.setStatus(ExecutionState.COMPLETED);
        success.setStatusCode(200);
        success.setExecutionTime(150L);
        results.add(success);

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getTotalRequests(), 2);
        Assert.assertEquals(perfResult.getSuccessRequests(), 1);
        Assert.assertEquals(perfResult.getFailedRequests(), 1);
        Assert.assertEquals(perfResult.getSuccessRate(), 50.0);
        Assert.assertTrue(perfResult.getErrors().contains("Connection refused"));
    }

    @Test(description = "Analyze empty results list")
    public void testAnalyzePerformanceResults_EmptyList() {
        List<HttpExecutionResult> results = Collections.emptyList();

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getTotalRequests(), 0);
        Assert.assertEquals(perfResult.getSuccessRequests(), 0);
        Assert.assertEquals(perfResult.getFailedRequests(), 0);
        Assert.assertEquals(perfResult.getSuccessRate(), 0.0);
        Assert.assertEquals(perfResult.getMinExecutionTime().longValue(), 0L);
        Assert.assertEquals(perfResult.getMaxExecutionTime().longValue(), 0L);
        Assert.assertEquals(perfResult.getP50().longValue(), 0L);
        Assert.assertEquals(perfResult.getP95().longValue(), 0L);
        Assert.assertEquals(perfResult.getP99().longValue(), 0L);
    }

    @Test(description = "Analyze null results list")
    public void testAnalyzePerformanceResults_NullList() {
        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(null);

        Assert.assertEquals(perfResult.getTotalRequests(), 0);
        Assert.assertEquals(perfResult.getSuccessRate(), 0.0);
    }

    @Test(description = "Analyze results with null elements in list")
    public void testAnalyzePerformanceResults_NullElements() {
        List<HttpExecutionResult> results = new ArrayList<>();
        results.add(null);

        HttpExecutionResult success = new HttpExecutionResult();
        success.setStatus(ExecutionState.COMPLETED);
        success.setStatusCode(200);
        success.setExecutionTime(100L);
        results.add(success);

        results.add(null);

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getTotalRequests(), 3);
        Assert.assertEquals(perfResult.getSuccessRequests(), 1);
        Assert.assertEquals(perfResult.getFailedRequests(), 2);
    }

    @Test(description = "Verify percentiles are calculated correctly")
    public void testAnalyzePerformanceResults_PercentilesCalculation() {
        // Create 100 results with execution times 1-100ms
        List<HttpExecutionResult> results = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            HttpExecutionResult result = new HttpExecutionResult();
            result.setStatus(ExecutionState.COMPLETED);
            result.setStatusCode(200);
            result.setExecutionTime((long) i);
            results.add(result);
        }

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getMinExecutionTime().longValue(), 1L);
        Assert.assertEquals(perfResult.getMaxExecutionTime().longValue(), 100L);
        // P50 should be around 50-51
        Assert.assertTrue(perfResult.getP50() >= 50 && perfResult.getP50() <= 51,
                "P50 should be around 50, got: " + perfResult.getP50());
        // P95 should be around 95-96
        Assert.assertTrue(perfResult.getP95() >= 95 && perfResult.getP95() <= 96,
                "P95 should be around 95, got: " + perfResult.getP95());
        // P99 should be around 99-100
        Assert.assertTrue(perfResult.getP99() >= 99 && perfResult.getP99() <= 100,
                "P99 should be around 99, got: " + perfResult.getP99());
    }

    @Test(description = "Verify errors are deduplicated")
    public void testAnalyzePerformanceResults_ErrorDeduplication() {
        List<HttpExecutionResult> results = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            HttpExecutionResult result = new HttpExecutionResult();
            result.setStatus(ExecutionState.SKIPPED);
            result.setExecutionTime(0L);
            result.setError("Connection timeout");
            results.add(result);
        }

        PerfExecResult perfResult = HttpResultAnalyzer.analyzePerformanceResults(results);

        Assert.assertEquals(perfResult.getFailedRequests(), 5);
        Assert.assertEquals(perfResult.getErrors().size(), 1, "Duplicate errors should be deduplicated");
        Assert.assertEquals(perfResult.getErrors().get(0), "Connection timeout");
    }

    // ==================== Helper Methods ====================

    private List<HttpExecutionResult> createResults(long[] times, int[] statusCodes, ExecutionState[] states) {
        List<HttpExecutionResult> results = new ArrayList<>();
        for (int i = 0; i < times.length; i++) {
            HttpExecutionResult result = new HttpExecutionResult();
            result.setExecutionTime(times[i]);
            result.setStatusCode(statusCodes[i]);
            result.setStatus(states[i]);
            if (statusCodes[i] >= 400) {
                result.setError("Failed request with status code: " + statusCodes[i]);
            }
            results.add(result);
        }
        return results;
    }
}
