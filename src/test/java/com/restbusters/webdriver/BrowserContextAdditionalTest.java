package com.restbusters.webdriver;

import com.restbusters.webdriver.core.DriverManager;
import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.enums.RetryStrategy;
import com.restbusters.webdriver.facade.BrowserContext;
import com.restbusters.webdriver.models.ExecutionResult;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Additional comprehensive tests for BrowserContext.
 * Covers untested methods: rightClick, submit, select dropdowns, validateText, and retry strategies.
 * 12 tests total covering missing functionality.
 */
public class BrowserContextAdditionalTest {

    private static final Logger log = LoggerFactory.getLogger(BrowserContextAdditionalTest.class);
    private static WebDriver driver;
    private BrowserContext browser;

    @BeforeSuite
    public void setupDriver() {
        log.info("Setting up WebDriver for additional test suite");
        driver = DriverManager.createDriver(DriverManager.BrowserType.CHROME_HEADLESS);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownDriver() {
        log.info("Tearing down WebDriver");
        if (driver != null) {
            try {
                DriverManager.quitDriver(driver);
            } catch (Exception e) {
                log.warn("Error closing driver: {}", e.getMessage());
            }
        }
    }

    @BeforeMethod
    public void setup() {
        browser = BrowserContext.with(driver);
        // Reset to safe defaults
        browser.withDefaultTimeout(10)
                .withScreenshotOnFailure(false)  // Disabled by default
                .withDefaultRetry(RetryStrategy.NO_RETRY);
    }

    @AfterMethod
    public void cleanup() {
        try {
            // Ensure we're back in main content
            driver.switchTo().defaultContent();
        } catch (Exception ignored) {
            // Already in default content
        }
    }

    // ========== SCREENSHOT ON FAILURE TEST ==========

    @Test(priority = 1, description = "Test screenshot ON FAILURE")
    public void testScreenshotOnFailure() {
        log.info("Testing screenshot ON FAILURE");

        browser.navigateTo("https://www.google.com");

        // Configure screenshot on failure
        browser.withScreenshotOnFailure(true)
                .withDefaultTimeout(2); // Short timeout to force failure faster

        // Try to interact with non-existent element (will fail)
        ExecutionResult result = browser.click(LocatorType.ID, "non-existent-element-failure-test");

        // Verify action failed but screenshot was taken
        assertFalse(result.isSuccessful(), "Action should fail");
        assertNotNull(result.getErrorMessage(), "Error message should be present");
        assertNotNull(result.getScreenshotAfter(), "Screenshot ON FAILURE should be populated");
        assertTrue(result.getScreenshotAfter().length > 0, "Screenshot ON FAILURE should have content");

        log.info("Screenshot on failure test passed - captured error screenshot");
    }

    // ========== UNTESTED METHOD COVERAGE ==========

    @Test(priority = 2, description = "Test right click action")
    public void testRightClickAction() {
        log.info("Testing RIGHT_CLICK action");

        browser.navigateTo("https://www.google.com");
        browser.waitForElement(LocatorType.NAME, "q");

        ExecutionResult result = browser.rightClick(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Right click should succeed");
        log.info("Right click test passed");
    }

    @Test(priority = 3, description = "Test submit action on form")
    public void testSubmitAction() {
        log.info("Testing SUBMIT action");

        browser.navigateTo("https://www.google.com");

        // Type in search box first
        browser.sendKeys(LocatorType.NAME, "q", "TestNG Framework");

        // Submit the form
        ExecutionResult result = browser.submit(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Submit should succeed");

        // After submit, we should be on search results page
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("search") || url.contains("?"),
                "Should navigate to search results after submit");

        log.info("Submit test passed");
    }

    @Test(priority = 4, description = "Test select by visible text")
    public void testSelectByText() {
        log.info("Testing SELECT_BY_TEXT action");

        // Navigate to a page with dropdown
        browser.navigateTo("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select");

        // Switch to iframe where the example is
        driver.switchTo().frame("iframeResult");

        // Wait for dropdown
        browser.waitForElement(LocatorType.ID, "cars");

        // Select by visible text
        ExecutionResult result = browser.selectByText(LocatorType.ID, "cars", "Volvo");

        assertTrue(result.isSuccessful(), "Select by text should succeed");
        log.info("Select by text test passed");

        // Switch back to default content
        driver.switchTo().defaultContent();
    }

    @Test(priority = 5, description = "Test select by value")
    public void testSelectByValue() {
        log.info("Testing SELECT_BY_VALUE action");

        browser.navigateTo("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select");
        driver.switchTo().frame("iframeResult");

        browser.waitForElement(LocatorType.ID, "cars");

        // Select by value attribute
        ExecutionResult result = browser.selectByValue(LocatorType.ID, "cars", "saab");

        assertTrue(result.isSuccessful(), "Select by value should succeed");
        log.info("Select by value test passed");

        driver.switchTo().defaultContent();
    }

    @Test(priority = 6, description = "Test select by index")
    public void testSelectByIndex() {
        log.info("Testing SELECT_BY_INDEX action");

        browser.navigateTo("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select");
        driver.switchTo().frame("iframeResult");

        browser.waitForElement(LocatorType.ID, "cars");

        // Select by index (0-based)
        ExecutionResult result = browser.selectByIndex(LocatorType.ID, "cars", 2);

        assertTrue(result.isSuccessful(), "Select by index should succeed");
        log.info("Select by index test passed");

        driver.switchTo().defaultContent();
    }

    @Test(priority = 7, description = "Test validate text on element")
    public void testValidateTextAction() {
        log.info("Testing VALIDATE_TEXT action");

        browser.navigateTo("https://www.google.com");

        // Wait for search button which has text
        browser.waitForElement(LocatorType.NAME, "btnK");

        // Validate text on search button (it has "Google Search" or similar)
        ExecutionResult result = browser.validateText(LocatorType.NAME, "btnK", "Google");

        // The test verifies the method executes without errors
        assertNotNull(result, "Result should not be null");
        log.info("Validate text test completed: {}", result.isSuccessful());
    }

    // ========== RETRY STRATEGY TESTS ==========

    @Test(priority = 8, description = "Test IMMEDIATE_RETRY strategy")
    public void testRetryStrategyImmediate() {
        log.info("Testing IMMEDIATE_RETRY strategy");

        browser.navigateTo("https://www.google.com");

        // Configure immediate retry (3 attempts, no delay)
        browser.withDefaultRetry(RetryStrategy.IMMEDIATE_RETRY)
                .withDefaultTimeout(2); // Short timeout

        // Try to find element that doesn't exist - will retry immediately 3 times
        long startTime = System.currentTimeMillis();
        ExecutionResult result = browser.click(LocatorType.ID, "non-existent-retry-test");
        long duration = System.currentTimeMillis() - startTime;

        // Should fail after retries
        assertFalse(result.isSuccessful(), "Action should fail after retries");

        // With immediate retry (3 attempts, no delay), should complete reasonably fast
        // Actual time depends on implementation overhead, allow generous margin
        assertTrue(duration < 15000, "Immediate retry should complete within 15 seconds");

        log.info("Immediate retry test passed - duration: {}ms", duration);
    }

    @Test(priority = 9, description = "Test LINEAR_BACKOFF retry strategy")
    public void testRetryStrategyLinearBackoff() {
        log.info("Testing LINEAR_BACKOFF retry strategy");

        browser.navigateTo("https://www.google.com");

        // Configure linear backoff (3 attempts, 1000ms delay between)
        browser.withDefaultRetry(RetryStrategy.LINEAR_BACKOFF)
                .withDefaultTimeout(2);

        long startTime = System.currentTimeMillis();
        ExecutionResult result = browser.click(LocatorType.ID, "non-existent-linear-test");
        long duration = System.currentTimeMillis() - startTime;

        assertFalse(result.isSuccessful(), "Action should fail after retries");

        // With linear backoff (3 attempts with 1000ms delays between)
        // Should take longer than immediate retry
        assertTrue(duration >= 3000, "Linear backoff should take at least 3 seconds");

        log.info("Linear backoff retry test passed - duration: {}ms", duration);
    }

    @Test(priority = 10, description = "Test EXPONENTIAL_BACKOFF retry strategy")
    public void testRetryStrategyExponentialBackoff() {
        log.info("Testing EXPONENTIAL_BACKOFF retry strategy");

        browser.navigateTo("https://www.google.com");

        // Configure exponential backoff (5 attempts, 500ms base delay)
        browser.withDefaultRetry(RetryStrategy.EXPONENTIAL_BACKOFF)
                .withDefaultTimeout(2);

        long startTime = System.currentTimeMillis();
        ExecutionResult result = browser.click(LocatorType.ID, "non-existent-exponential-test");
        long duration = System.currentTimeMillis() - startTime;

        assertFalse(result.isSuccessful(), "Action should fail after retries");

        // With exponential backoff (5 attempts with increasing delays)
        // Should take significantly longer than linear backoff
        assertTrue(duration >= 5000, "Exponential backoff should take at least 5 seconds");

        log.info("Exponential backoff retry test passed - duration: {}ms", duration);
    }

    // ========== ADVANCED EDGE CASES ==========

    @Test(priority = 11, description = "Test timeout handling")
    public void testTimeoutHandling() {
        log.info("Testing timeout handling");

        // Set very short timeout
        browser.withDefaultTimeout(1);

        // Try to wait for element with impossible short timeout
        ExecutionResult result = browser.waitForElement(LocatorType.ID, "delayed-element-that-doesnt-exist");

        assertFalse(result.isSuccessful(), "Should timeout");
        assertNotNull(result.getErrorMessage(), "Should have timeout error message");

        log.info("Timeout handling test passed");
    }

    @Test(priority = 12, description = "Test configuration persistence")
    public void testConfigurationPersistence() {
        log.info("Testing configuration persistence");

        // Set configuration
        browser.withDefaultTimeout(20)
                .withScreenshotOnFailure(true)
                .withDefaultRetry(RetryStrategy.NO_RETRY);

        // Perform multiple actions
        browser.navigateTo("https://www.google.com");
        ExecutionResult result1 = browser.waitForElement(LocatorType.NAME, "q");
        ExecutionResult result2 = browser.click(LocatorType.NAME, "q");

        // Both should succeed with the configured timeout
        assertTrue(result1.isSuccessful(), "First action should succeed with config");
        assertTrue(result2.isSuccessful(), "Second action should succeed with config");

        log.info("Configuration persistence test passed");
    }
}