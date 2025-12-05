package com.restbusters.webdriver.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author amatsaylo on 9/26/25
 * @project qreasp
 * Represents the complete result of executing a WebDriver action.
 * Contains timing information, success status, and any validation results.
 */

@Data
@Builder
@Jacksonized
public class ExecutionResult {
    private boolean successful;
    private String actionType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration executionDuration;
    private String errorMessage;
    private String stackTrace;
    private byte[] screenshotBefore;
    private byte[] screenshotAfter;
    private String elementLocator;
    private String resultValue; // For actions that return values

    @Singular
    private List<ValidationResult> validationResults;

    /**
     * Creates a successful execution result.
     */
    public static ExecutionResult success(String actionType, LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        return ExecutionResult.builder()
                .successful(true)
                .actionType(actionType)
                .startTime(startTime)
                .endTime(endTime)
                .executionDuration(Duration.between(startTime, endTime))
                .build();
    }

    /**
     * Creates a failed execution result.
     */
    public static ExecutionResult failure(String actionType, LocalDateTime startTime, String errorMessage, Exception exception) {
        LocalDateTime endTime = LocalDateTime.now();
        return ExecutionResult.builder()
                .successful(false)
                .actionType(actionType)
                .startTime(startTime)
                .endTime(endTime)
                .executionDuration(Duration.between(startTime, endTime))
                .errorMessage(errorMessage)
                .stackTrace(exception != null ? getStackTraceAsString(exception) : null)
                .build();
    }

    private static String getStackTraceAsString(Exception exception) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
}
