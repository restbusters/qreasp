package com.restbusters.webdriver.models;

import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.enums.DriverType;
import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.enums.RetryStrategy;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Tests for WebDriverState model class.
 */
public class WebDriverStateTest {

    @Test(description = "Test create factory method")
    public void testCreateFactoryMethod() {
        WebDriverState state = WebDriverState.create();

        assertNotNull(state, "State should not be null");
        assertNotNull(state.getCreatedAt(), "Created timestamp should be set");
        assertNull(state.getWebDriver(), "WebDriver should be null");
    }

    @Test(description = "Test default values")
    public void testDefaultValues() {
        WebDriverState state = WebDriverState.builder().build();

        assertEquals(state.getTimeoutInSeconds(), 10, "Default timeout should be 10");
        assertEquals(state.getPollingIntervalMs(), 500, "Default polling interval should be 500");
        assertEquals(state.getFrameIndex(), -1, "Default frame index should be -1");
        assertEquals(state.getRetryStrategy(), RetryStrategy.NO_RETRY, "Default retry should be NO_RETRY");
    }

    @Test(description = "Test builder pattern")
    public void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();

        WebDriverState state = WebDriverState.builder()
                .driverType(DriverType.CHROME)
                .currentTitle("Test Page")
                .currentUrl("https://example.com")
                .expectedTitle("Expected Title")
                .expectedUrl("https://expected.com")
                .actionType(ActionType.CLICK)
                .value("test-value")
                .timeoutInSeconds(30)
                .pollingIntervalMs(1000)
                .retryStrategy(RetryStrategy.LINEAR_BACKOFF)
                .takeScreenshotBefore(true)
                .takeScreenshotAfter(true)
                .takeScreenshotOnFailure(true)
                .createdAt(now)
                .build();

        assertEquals(state.getDriverType(), DriverType.CHROME);
        assertEquals(state.getCurrentTitle(), "Test Page");
        assertEquals(state.getCurrentUrl(), "https://example.com");
        assertEquals(state.getExpectedTitle(), "Expected Title");
        assertEquals(state.getExpectedUrl(), "https://expected.com");
        assertEquals(state.getActionType(), ActionType.CLICK);
        assertEquals(state.getValue(), "test-value");
        assertEquals(state.getTimeoutInSeconds(), 30);
        assertEquals(state.getPollingIntervalMs(), 1000);
        assertEquals(state.getRetryStrategy(), RetryStrategy.LINEAR_BACKOFF);
        assertTrue(state.isTakeScreenshotBefore());
        assertTrue(state.isTakeScreenshotAfter());
        assertTrue(state.isTakeScreenshotOnFailure());
        assertEquals(state.getCreatedAt(), now);
    }

    @Test(description = "Test fluent withAction method")
    public void testWithActionMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withAction(ActionType.SEND_KEYS);

        assertEquals(newState.getActionType(), ActionType.SEND_KEYS);
    }

    @Test(description = "Test fluent withLocator method")
    public void testWithLocatorMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withLocator(LocatorType.CSS_SELECTOR, ".my-class");

        assertNotNull(newState.getLocator());
        assertEquals(newState.getLocatorType(), LocatorType.CSS_SELECTOR);
        assertEquals(newState.getLocatorValue(), ".my-class");
    }

    @Test(description = "Test fluent withValue method")
    public void testWithValueMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withValue("test-input");

        assertEquals(newState.getValue(), "test-input");
    }

    @Test(description = "Test fluent withTimeout method")
    public void testWithTimeoutMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withTimeout(60);

        assertEquals(newState.getTimeoutInSeconds(), 60);
    }

    @Test(description = "Test fluent withRetry method")
    public void testWithRetryMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withRetry(RetryStrategy.EXPONENTIAL_BACKOFF);

        assertEquals(newState.getRetryStrategy(), RetryStrategy.EXPONENTIAL_BACKOFF);
    }

    @Test(description = "Test fluent withScreenshots method")
    public void testWithScreenshotsMethod() {
        WebDriverState state = WebDriverState.create();

        WebDriverState newState = state.withScreenshots(true, false, true);

        assertTrue(newState.isTakeScreenshotBefore());
        assertFalse(newState.isTakeScreenshotAfter());
        assertTrue(newState.isTakeScreenshotOnFailure());
    }

    @Test(description = "Test getLocatorString")
    public void testGetLocatorString() {
        WebDriverState state = WebDriverState.create()
                .withLocator(LocatorType.XPATH, "//div[@id='test']");

        String locatorString = state.getLocatorString();

        assertEquals(locatorString, "XPATH: //div[@id='test']");
    }

    @Test(description = "Test getLocatorString with null locator")
    public void testGetLocatorStringWithNullLocator() {
        WebDriverState state = WebDriverState.create();

        assertNull(state.getLocatorString());
    }

    @Test(description = "Test getLocatorType with null locator")
    public void testGetLocatorTypeWithNullLocator() {
        WebDriverState state = WebDriverState.create();

        assertNull(state.getLocatorType());
    }

    @Test(description = "Test getLocatorValue with null locator")
    public void testGetLocatorValueWithNullLocator() {
        WebDriverState state = WebDriverState.create();

        assertNull(state.getLocatorValue());
    }

    @Test(description = "Test hasValidationErrors with no errors")
    public void testHasValidationErrorsNoErrors() {
        ValidationResult success = ValidationResult.success("TEXT", "Matched");

        WebDriverState state = WebDriverState.builder()
                .validationResult(success)
                .build();

        assertFalse(state.hasValidationErrors());
    }

    @Test(description = "Test hasValidationErrors with errors")
    public void testHasValidationErrorsWithErrors() {
        ValidationResult failure = ValidationResult.failure("TEXT", "expected", "actual", "Mismatch");

        WebDriverState state = WebDriverState.builder()
                .validationResult(failure)
                .build();

        assertTrue(state.hasValidationErrors());
    }

    @Test(description = "Test hasValidationErrors with null results")
    public void testHasValidationErrorsWithNullResults() {
        WebDriverState state = WebDriverState.builder().build();

        assertFalse(state.hasValidationErrors());
    }

    @Test(description = "Test wasLastExecutionSuccessful with success")
    public void testWasLastExecutionSuccessfulWithSuccess() {
        ExecutionResult success = ExecutionResult.success("CLICK", LocalDateTime.now());

        WebDriverState state = WebDriverState.builder()
                .lastExecutionResult(success)
                .build();

        assertTrue(state.wasLastExecutionSuccessful());
    }

    @Test(description = "Test wasLastExecutionSuccessful with failure")
    public void testWasLastExecutionSuccessfulWithFailure() {
        ExecutionResult failure = ExecutionResult.failure("CLICK", LocalDateTime.now(), "Error", null);

        WebDriverState state = WebDriverState.builder()
                .lastExecutionResult(failure)
                .build();

        assertFalse(state.wasLastExecutionSuccessful());
    }

    @Test(description = "Test wasLastExecutionSuccessful with null result")
    public void testWasLastExecutionSuccessfulWithNullResult() {
        WebDriverState state = WebDriverState.builder().build();

        assertFalse(state.wasLastExecutionSuccessful());
    }

    @Test(description = "Test getSummary")
    public void testGetSummary() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.NAVIGATE_TO)
                .currentUrl("https://example.com")
                .build();

        String summary = state.getSummary();

        assertNotNull(summary);
        assertTrue(summary.contains("NAVIGATE_TO"));
        assertTrue(summary.contains("example.com"));
    }

    @Test(description = "Test toBuilder creates copy")
    public void testToBuilderCreatesCopy() {
        WebDriverState original = WebDriverState.builder()
                .actionType(ActionType.CLICK)
                .value("original")
                .timeoutInSeconds(10)
                .build();

        WebDriverState copy = original.toBuilder()
                .value("modified")
                .build();

        assertEquals(original.getValue(), "original");
        assertEquals(copy.getValue(), "modified");
        assertEquals(copy.getActionType(), ActionType.CLICK);
        assertEquals(copy.getTimeoutInSeconds(), 10);
    }

    @Test(description = "Test form data list with singular builder")
    public void testFormDataListWithSingularBuilder() {
        FormData field1 = new FormData(LocatorType.ID, "username", "user1");
        FormData field2 = new FormData(LocatorType.ID, "password", "pass1");

        WebDriverState state = WebDriverState.builder()
                .formData(field1)
                .formData(field2)
                .build();

        assertNotNull(state.getFormDataList());
        assertEquals(state.getFormDataList().size(), 2);
    }

    @Test(description = "Test dropdown selection fields")
    public void testDropdownSelectionFields() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.SELECT_FROM_DROPDOWN)
                .dropdownValue("option1")
                .dropdownText("Option One")
                .dropdownIndex(0)
                .build();

        assertEquals(state.getDropdownValue(), "option1");
        assertEquals(state.getDropdownText(), "Option One");
        assertEquals(state.getDropdownIndex(), Integer.valueOf(0));
    }

    @Test(description = "Test window operation fields")
    public void testWindowOperationFields() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.SET_WINDOW_SIZE)
                .windowWidth(1920)
                .windowHeight(1080)
                .windowHandle("main-window")
                .build();

        assertEquals(state.getWindowWidth(), Integer.valueOf(1920));
        assertEquals(state.getWindowHeight(), Integer.valueOf(1080));
        assertEquals(state.getWindowHandle(), "main-window");
    }

    @Test(description = "Test frame operation fields")
    public void testFrameOperationFields() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.SWITCH_TO_FRAME)
                .frameNameOrId("my-frame")
                .frameIndex(2)
                .build();

        assertEquals(state.getFrameNameOrId(), "my-frame");
        assertEquals(state.getFrameIndex(), 2);
    }

    @Test(description = "Test JavaScript execution fields")
    public void testJavaScriptExecutionFields() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.EXECUTE_JAVASCRIPT)
                .javascriptCode("return document.title;")
                .javascriptArgument("arg1")
                .javascriptArgument("arg2")
                .build();

        assertEquals(state.getJavascriptCode(), "return document.title;");
        assertNotNull(state.getJavascriptArguments());
        assertEquals(state.getJavascriptArguments().size(), 2);
    }

    @Test(description = "Test mouse action fields")
    public void testMouseActionFields() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.DRAG_AND_DROP)
                .locator(Map.of("locatorType", "ID", "locatorValue", "source"))
                .targetLocator(Map.of("locatorType", "ID", "locatorValue", "target"))
                .offsetX(10)
                .offsetY(20)
                .build();

        assertNotNull(state.getTargetLocator());
        assertEquals(state.getOffsetX(), Integer.valueOf(10));
        assertEquals(state.getOffsetY(), Integer.valueOf(20));
    }

    @Test(description = "Test file upload field")
    public void testFileUploadField() {
        WebDriverState state = WebDriverState.builder()
                .actionType(ActionType.UPLOAD_FILE)
                .filePath("/path/to/file.pdf")
                .build();

        assertEquals(state.getFilePath(), "/path/to/file.pdf");
    }
}
