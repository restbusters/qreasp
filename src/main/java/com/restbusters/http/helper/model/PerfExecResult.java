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

    @Override
    public String toString() {
        return "Canary Performance Results:\n" +
                "  Total Requests: " + totalRequests + "\n" +
                "  Successful Requests: " + successRequests + "\n" +
                "  Failed Requests: " + failedRequests + "\n" +
                "  Average Time: " + averageTime + " ms\n" +
                "  Errors: " + (errors.isEmpty() ? "None" : errors.size() + " errors");
    }
}