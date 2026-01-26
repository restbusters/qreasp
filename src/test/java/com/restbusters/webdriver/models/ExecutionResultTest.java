package com.restbusters.webdriver.models;

import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Tests for ExecutionResult model class.
 */
public class ExecutionResultTest {

    @Test(description = "Test success factory method")
    public void testSuccessFactoryMethod() {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(1);

        ExecutionResult result = ExecutionResult.success("CLICK", startTime);

        assertTrue(result.isSuccessful(), "Should be successful");
        assertEquals(result.getActionType(), "CLICK");
        assertEquals(result.getStartTime(), startTime);
        assertNotNull(result.getEndTime(), "End time should be set");
        assertNotNull(result.getExecutionDuration(), "Duration should be set");
        assertTrue(result.getExecutionDuration().toMillis() >= 0, "Duration should be positive");
        assertNull(result.getErrorMessage(), "Error message should be null for success");
        assertNull(result.getStackTrace(), "Stack trace should be null for success");
    }

    @Test(description = "Test failure factory method with exception")
    public void testFailureFactoryMethodWithException() {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(1);
        Exception exception = new RuntimeException("Test error");

        ExecutionResult result = ExecutionResult.failure("SEND_KEYS", startTime, "Element not found", exception);

        assertFalse(result.isSuccessful(), "Should not be successful");
        assertEquals(result.getActionType(), "SEND_KEYS");
        assertEquals(result.getStartTime(), startTime);
        assertNotNull(result.getEndTime(), "End time should be set");
        assertNotNull(result.getExecutionDuration(), "Duration should be set");
        assertEquals(result.getErrorMessage(), "Element not found");
        assertNotNull(result.getStackTrace(), "Stack trace should be set");
        assertTrue(result.getStackTrace().contains("RuntimeException"), "Stack trace should contain exception type");
        assertTrue(result.getStackTrace().contains("Test error"), "Stack trace should contain error message");
    }

    @Test(description = "Test failure factory method without exception")
    public void testFailureFactoryMethodWithoutException() {
        LocalDateTime startTime = LocalDateTime.now();

        ExecutionResult result = ExecutionResult.failure("VALIDATE_TEXT", startTime, "Text mismatch", null);

        assertFalse(result.isSuccessful(), "Should not be successful");
        assertEquals(result.getErrorMessage(), "Text mismatch");
        assertNull(result.getStackTrace(), "Stack trace should be null when no exception");
    }

    @Test(description = "Test builder pattern")
    public void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();
        byte[] screenshot = new byte[] { 1, 2, 3 };

        ExecutionResult result = ExecutionResult.builder()
                .successful(true)
                .actionType("NAVIGATE_TO")
                .startTime(now)
                .endTime(now.plusSeconds(2))
                .executionDuration(Duration.ofSeconds(2))
                .screenshotBefore(screenshot)
                .screenshotAfter(screenshot)
                .elementLocator("css: .my-class")
                .resultValue("https://example.com")
                .build();

        assertTrue(result.isSuccessful());
        assertEquals(result.getActionType(), "NAVIGATE_TO");
        assertEquals(result.getStartTime(), now);
        assertEquals(result.getEndTime(), now.plusSeconds(2));
        assertEquals(result.getExecutionDuration(), Duration.ofSeconds(2));
        assertNotNull(result.getScreenshotBefore());
        assertNotNull(result.getScreenshotAfter());
        assertEquals(result.getElementLocator(), "css: .my-class");
        assertEquals(result.getResultValue(), "https://example.com");
    }

    @Test(description = "Test builder with validation results")
    public void testBuilderWithValidationResults() {
        ValidationResult validation1 = ValidationResult.success("TEXT", "Text matches");
        ValidationResult validation2 = ValidationResult.failure("ATTRIBUTE", "expected", "actual", "Attribute mismatch");

        ExecutionResult result = ExecutionResult.builder()
                .successful(false)
                .actionType("VALIDATE")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .validationResult(validation1)
                .validationResult(validation2)
                .build();

        assertNotNull(result.getValidationResults());
        assertEquals(result.getValidationResults().size(), 2);
        assertTrue(result.getValidationResults().get(0).isPassed());
        assertFalse(result.getValidationResults().get(1).isPassed());
    }

    @Test(description = "Test execution duration calculation")
    public void testExecutionDurationCalculation() {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(5);

        ExecutionResult result = ExecutionResult.success("WAIT", startTime);

        assertTrue(result.getExecutionDuration().toMillis() >= 4900,
            "Duration should be at least 4.9 seconds");
    }

    @Test(description = "Test different action types")
    public void testDifferentActionTypes() {
        String[] actionTypes = {
            "NAVIGATE_TO", "CLICK", "DOUBLE_CLICK", "RIGHT_CLICK",
            "SEND_KEYS", "CLEAR", "SUBMIT", "GET_TEXT",
            "WAIT_FOR_ELEMENT", "WAIT_FOR_VISIBLE", "WAIT_FOR_CLICKABLE",
            "VALIDATE_TEXT", "VALIDATE_TITLE", "FILL_FORM",
            "TAKE_SCREENSHOT", "MAXIMIZE_WINDOW", "NAVIGATE_BACK", "REFRESH"
        };

        for (String actionType : actionTypes) {
            ExecutionResult result = ExecutionResult.success(actionType, LocalDateTime.now());
            assertEquals(result.getActionType(), actionType);
        }
    }

    @Test(description = "Test result value for GET actions")
    public void testResultValueForGetActions() {
        ExecutionResult result = ExecutionResult.builder()
                .successful(true)
                .actionType("GET_TEXT")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .resultValue("Button Label")
                .build();

        assertEquals(result.getResultValue(), "Button Label");
    }

    @Test(description = "Test screenshot data")
    public void testScreenshotData() {
        byte[] beforeScreenshot = "before".getBytes();
        byte[] afterScreenshot = "after".getBytes();

        ExecutionResult result = ExecutionResult.builder()
                .successful(true)
                .actionType("CLICK")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .screenshotBefore(beforeScreenshot)
                .screenshotAfter(afterScreenshot)
                .build();

        assertNotNull(result.getScreenshotBefore());
        assertNotNull(result.getScreenshotAfter());
        assertEquals(result.getScreenshotBefore().length, beforeScreenshot.length);
        assertEquals(result.getScreenshotAfter().length, afterScreenshot.length);
    }
}
