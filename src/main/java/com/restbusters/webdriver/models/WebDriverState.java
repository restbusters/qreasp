package com.restbusters.webdriver.models;

/**
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.enums.DriverType;
import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.enums.RetryStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import org.openqa.selenium.WebDriver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Central state object that contains all information needed to execute a WebDriver action.
 * This is the core of the state-driven testing approach.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */

@Data
@Builder(toBuilder = true)
@Jacksonized
public class WebDriverState {
    // Core WebDriver properties
    @JsonIgnore // Don't serialize WebDriver instance
    private WebDriver webDriver;
    private DriverType driverType;

    // Page state tracking
    private String currentTitle;
    private String expectedTitle;
    private String currentUrl;
    private String expectedUrl;

    // Action configuration
    private ActionType actionType;
    private Map<String, String> locator;

    // Action data
    private String value;
    private String attributeName;
    private String expectedText;

    // Form handling
    @Singular("formData")
    private List<FormData> formDataList;

    // Dropdown selection
    private String dropdownValue;
    private String dropdownText;
    private Integer dropdownIndex;

    // Wait configuration
    @Builder.Default
    private int timeoutInSeconds = 10;
    @Builder.Default
    private int pollingIntervalMs = 500;

    // Navigation
    private String url;

    // Window/Frame operations
    private String windowHandle;
    private String frameNameOrId;
    @Builder.Default
    private int frameIndex = -1;
    private Integer windowWidth;
    private Integer windowHeight;

    // Mouse actions
    private Map<String, String> targetLocator;
    private Integer offsetX;
    private Integer offsetY;

    // JavaScript execution
    private String javascriptCode;
    @Singular("javascriptArgument")
    private List<Object> javascriptArguments;

    // File operations
    private String filePath;

    // Screenshot configuration
    private boolean takeScreenshotBefore;
    private boolean takeScreenshotAfter;
    private boolean takeScreenshotOnFailure;

    // Retry configuration
    @Builder.Default
    private RetryStrategy retryStrategy = RetryStrategy.NO_RETRY;

    // Execution tracking
    private LocalDateTime createdAt;
    private LocalDateTime lastExecutedAt;

    @JsonIgnore // Don't serialize execution result to avoid circular references
    private ExecutionResult lastExecutionResult;

    // Validation
    @Singular("validationResult")
    private List<ValidationResult> validationResults;

    /**
     * Creates a new WebDriverState with current timestamp.
     */
    public static WebDriverState create() {
        return WebDriverState.builder()
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a new state with the provided WebDriver instance.
     */
    public static WebDriverState create(WebDriver webDriver) {
        return WebDriverState.builder()
                .webDriver(webDriver)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Fluent builder methods for ease of use
    public WebDriverState withWebDriver(WebDriver webDriver) {
        return this.toBuilder().webDriver(webDriver).build();
    }

    public WebDriverState withAction(ActionType actionType) {
        return this.toBuilder().actionType(actionType).build();
    }

    public WebDriverState withLocator(LocatorType locatorType, String locatorValue) {
        return this.toBuilder()
                .locator(Map.of("locatorType", locatorType.name(), "locatorValue", locatorValue))
                .build();
    }

    public WebDriverState withValue(String value) {
        return this.toBuilder().value(value).build();
    }

    public WebDriverState withTimeout(int timeoutInSeconds) {
        return this.toBuilder().timeoutInSeconds(timeoutInSeconds).build();
    }

    public WebDriverState withRetry(RetryStrategy retryStrategy) {
        return this.toBuilder().retryStrategy(retryStrategy).build();
    }

    public WebDriverState withScreenshots(boolean before, boolean after, boolean onFailure) {
        return this.toBuilder()
                .takeScreenshotBefore(before)
                .takeScreenshotAfter(after)
                .takeScreenshotOnFailure(onFailure)
                .build();
    }

    // Helper methods
    public LocatorType getLocatorType() {
        return locator != null ? LocatorType.valueOf(locator.get("locatorType")) : null;
    }

    public String getLocatorValue() {
        return locator != null ? locator.get("locatorValue") : null;
    }

    public String getLocatorString() {
        if (locator == null) return null;
        return String.format("%s: %s", locator.get("locatorType"), locator.get("locatorValue"));
    }

    /**
     * Updates the current page state from the WebDriver.
     */
    public void updatePageState() {
        if (webDriver != null) {
            this.currentTitle = webDriver.getTitle();
            this.currentUrl = webDriver.getCurrentUrl();
            this.lastExecutedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if there are any validation errors in the current state.
     */
    public boolean hasValidationErrors() {
        return validationResults != null &&
                validationResults.stream().anyMatch(result -> !result.isPassed());
    }

    /**
     * Checks if the last execution was successful.
     */
    public boolean wasLastExecutionSuccessful() {
        return lastExecutionResult != null && lastExecutionResult.isSuccessful();
    }

    /**
     * Creates a summary string of the current state for logging.
     */
    public String getSummary() {
        return String.format("WebDriverState[action=%s, locator=%s, url=%s]",
                actionType, getLocatorString(), currentUrl);
    }
}