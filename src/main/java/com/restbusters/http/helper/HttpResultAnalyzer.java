package com.restbusters.http.helper;

import com.restbusters.http.helper.model.HttpExecutionResult;
import com.restbusters.http.helper.model.PerfExecResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Set min/max execution time and percentiles
        if (results != null && !results.isEmpty()) {
            List<Long> sortedTimes = results.stream()
                    .filter(r -> r != null && r.getExecutionTime() != null)
                    .map(HttpExecutionResult::getExecutionTime)
                    .sorted()
                    .collect(Collectors.toList());

            if (!sortedTimes.isEmpty()) {
                perfResult.setMinExecutionTime(sortedTimes.get(0));
                perfResult.setMaxExecutionTime(sortedTimes.get(sortedTimes.size() - 1));
                perfResult.setP50(calculatePercentile(sortedTimes, 50));
                perfResult.setP95(calculatePercentile(sortedTimes, 95));
                perfResult.setP99(calculatePercentile(sortedTimes, 99));
            } else {
                setDefaultTimeValues(perfResult);
            }
        } else {
            setDefaultTimeValues(perfResult);
        }
        return perfResult;
    }

    /**
     * Calculates the percentile value from a sorted list of execution times.
     * Uses linear interpolation for more accurate percentile calculation.
     *
     * @param sortedTimes Sorted list of execution times in ascending order
     * @param percentile  The percentile to calculate (0-100)
     * @return The percentile value
     */
    public static Long calculatePercentile(List<Long> sortedTimes, double percentile) {
        if (sortedTimes == null || sortedTimes.isEmpty()) {
            return 0L;
        }
        if (sortedTimes.size() == 1) {
            return sortedTimes.get(0);
        }

        double index = (percentile / 100.0) * (sortedTimes.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        if (lowerIndex == upperIndex) {
            return sortedTimes.get(lowerIndex);
        }

        // Linear interpolation
        double fraction = index - lowerIndex;
        long lowerValue = sortedTimes.get(lowerIndex);
        long upperValue = sortedTimes.get(upperIndex);
        return Math.round(lowerValue + fraction * (upperValue - lowerValue));
    }

    private static void setDefaultTimeValues(PerfExecResult perfResult) {
        perfResult.setMaxExecutionTime(0L);
        perfResult.setMinExecutionTime(0L);
        perfResult.setP50(0L);
        perfResult.setP95(0L);
        perfResult.setP99(0L);
    }
}
