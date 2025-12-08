package com.restbusters.webdriver.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import java.time.LocalDateTime;

/**
 * @author amatsaylo on 9/26/25
 * @project qreasp
 * Represents the result of a validation operation performed during test execution.
 */
@Data
@Builder
@Jacksonized
public class ValidationResult {
    private boolean passed;
    private String validationType;
    private String expectedValue;
    private String actualValue;
    private String message;
    private LocalDateTime timestamp;
    private String elementLocator;

    /**
     * Creates a successful validation result.
     */
    public static ValidationResult success(String validationType, String message) {
        return ValidationResult.builder()
                .passed(true)
                .validationType(validationType)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a failed validation result.
     */
    public static ValidationResult failure(String validationType, String expected, String actual, String message) {
        return ValidationResult.builder()
                .passed(false)
                .validationType(validationType)
                .expectedValue(expected)
                .actualValue(actual)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}