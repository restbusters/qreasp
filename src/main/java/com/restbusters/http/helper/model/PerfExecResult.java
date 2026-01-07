package com.restbusters.http.helper.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PerfExecResult {
    private int totalRequests;
    private int failedRequests;
    private int successRequests;
    private List<String> errors;
    private double averageTime;
    private List<HttpExecutionResult> results;
    private Long maxExecutionTime;
    private Long minExecutionTime;
    private double successRate;
    private Long p50;
    private Long p95;
    private Long p99;

    @Override
    public String toString() {
        return "Performance Results:\n" +
                "  Total Requests: " + totalRequests + "\n" +
                "  Successful Requests: " + successRequests + "\n" +
                "  Failed Requests: " + failedRequests + "\n" +
                "  Success Rate: " + successRate + "%\n" +
                "  Min Time: " + minExecutionTime + " ms\n" +
                "  Average Time: " + averageTime + " ms\n" +
                "  P50 (Median): " + p50 + " ms\n" +
                "  P95: " + p95 + " ms\n" +
                "  P99: " + p99 + " ms\n" +
                "  Max Time: " + maxExecutionTime + " ms\n" +
                "  Errors: " + (errors == null || errors.isEmpty() ? "None" : errors.size() + " errors");
    }
}