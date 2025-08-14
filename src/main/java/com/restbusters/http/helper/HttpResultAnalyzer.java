package com.restbusters.http.helper;

import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.http.helper.model.PerfExecResult;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HttpResultAnalyzer {

    /**
     * Analyzes performance test results and returns a summary
     * @param results List of HttpExecutionResult objects
     * @return PerfExecResult containing performance metrics
     */
    public static PerfExecResult analyzePerformanceResults_orig(List<HttpExecutionResult> results) {
        PerfExecResult perfResult = new PerfExecResult();
        List<String> errors = new ArrayList<>();

        int totalRequests = results.size();
        int successRequests = 0;
        int failedRequests = 0;
        long totalTime = 0;
        int validTimeCount = 0;

        for (HttpExecutionResult result : results) {
            // Count success/failure based on status and HTTP status code
            if (result.getStatus() == ExecutionState.COMPLETED &&
                    (result.getStatusCode() >= 200 && result.getStatusCode() < 300)) {
                successRequests++;
            } else {
                failedRequests++;
                if (result.getError() != null && !result.getError().isEmpty()) {
                    errors.add(result.getError());
                } else {
                    errors.add("Failed request with status code: " + result.getStatusCode());
                }
            }

            // Calculate time
            if (result.getExecutionTime() != null) {
                totalTime += result.getExecutionTime();
                validTimeCount++;
            }
        }

        double avgTime = validTimeCount > 0 ? (double) totalTime / validTimeCount : 0;

        perfResult.setTotalRequests(totalRequests);
        perfResult.setSuccessRequests(successRequests);
        perfResult.setFailedRequests(failedRequests);
        perfResult.setErrors(errors);
        perfResult.setAverageTime(Math.round(avgTime * 100.0) / 100.0);
        perfResult.setMaxExecutionTime(
                results.stream().mapToLong(HttpExecutionResult::getExecutionTime).max().orElse(0)
        );
        perfResult.setMinExecutionTime(
                results.stream().mapToLong(HttpExecutionResult::getExecutionTime).min().orElse(0)
        );

        return perfResult;
    }


    public static PerfExecResult analyzePerformanceResults(List<HttpExecutionResult> results) {
        PerfExecResult perfResult = new PerfExecResult();
        // Use LinkedHashSet to avoid duplicate errors and preserve order
        Set<String> errors = new LinkedHashSet<>();
        int totalRequests = results != null ? results.size() : 0;
        int successRequests = 0;
        int failedRequests = 0;
        long totalTime = 0;
        int validTimeCount = 0;

        if (results != null) {
            for (HttpExecutionResult result : results) {
                if (result == null) {
                    failedRequests++;
                    errors.add("Null result object in results list.");
                    continue;
                }
                // Count success/failure based on status and HTTP status code
                if (result.getStatus() == ExecutionState.COMPLETED &&
                        result.getStatusCode() >= 200 && result.getStatusCode() < 300) {
                    successRequests++;
                } else {
                    failedRequests++;
                    if (result.getError() != null && !result.getError().isEmpty()) {
                        errors.add(result.getError());
                    } else {
                        errors.add("Failed request with status code: " + result.getStatusCode());
                    }
                }

                // Calculate time
                if (result.getExecutionTime() != null) {
                    totalTime += result.getExecutionTime();
                    validTimeCount++;
                }
            }
        }

        double avgTime = validTimeCount > 0 ? (double) totalTime / validTimeCount : 0;

        // Calculate success rate as percentage
        double successRate = totalRequests > 0 ?
                (double) successRequests / totalRequests * 100 : 0;
        // Round to 2 decimal places
        double roundedSuccessRate = Math.round(successRate * 100.0) / 100.0;

        perfResult.setTotalRequests(totalRequests);
        perfResult.setSuccessRequests(successRequests);
        perfResult.setFailedRequests(failedRequests);
        perfResult.setErrors(new ArrayList<>(errors));
        perfResult.setAverageTime(Math.round(avgTime * 100.0) / 100.0);
        perfResult.setSuccessRate(roundedSuccessRate); // Add this line

        // Set min/max execution time
        if (results != null && !results.isEmpty()) {
            perfResult.setMaxExecutionTime(
                    results.stream()
                            .filter(r -> r != null && r.getExecutionTime() != null)
                            .mapToLong(HttpExecutionResult::getExecutionTime)
                            .max().orElse(0L)
            );
            perfResult.setMinExecutionTime(
                    results.stream()
                            .filter(r -> r != null && r.getExecutionTime() != null)
                            .mapToLong(HttpExecutionResult::getExecutionTime)
                            .min().orElse(0L)
            );
        } else {
            perfResult.setMaxExecutionTime(0L);
            perfResult.setMinExecutionTime(0L);
        }
        return perfResult;
    }
}
