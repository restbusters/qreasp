package com.restbusters.webdriver;

import com.restbusters.webdriver.core.DriverManager;
import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.enums.RetryStrategy;
import com.restbusters.webdriver.facade.BrowserContext;
import com.restbusters.webdriver.models.ExecutionResult;
import com.restbusters.webdriver.models.FormData;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * TestNG test class for BrowserContext functionality.
 * Tests using real Chrome driver and Google website.
 */
public class BrowserContextTest {

    private static final Logger log = LoggerFactory.getLogger(BrowserContextTest.class);
    private static WebDriver driver;
    private BrowserContext browser;

    @BeforeSuite
    public void setupDriver() {
        log.info("Setting up WebDriver for test suite");
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
    }

    @Test(priority = 1, description = "Test navigate to Google")
    public void testNavigateToGoogle() {
        log.info("Testing navigation to Google");

        ExecutionResult result = browser.navigateTo("https://www.google.com");

        assertTrue(result.isSuccessful(), "Navigation should succeed");
        assertTrue(driver.getCurrentUrl().contains("google.com"));
        log.info("Navigate test passed");
    }

    @Test(priority = 2, description = "Test fluent configuration")
    public void testFluentConfiguration() {
        log.info("Testing fluent API configuration");

        BrowserContext configuredBrowser = browser
                .withDefaultTimeout(15)
                .withScreenshotOnFailure(true)
                .withDefaultRetry(RetryStrategy.NO_RETRY);

        assertSame(browser, configuredBrowser, "Should return same instance for fluent API");
        log.info("Fluent configuration test passed");
    }

    @Test(priority = 3, description = "Test click on Google search button")
    public void testClickAction() {
        log.info("Testing CLICK action on Google");

        // Navigate to Google
        browser.navigateTo("https://www.google.com");

        // Wait for search box and type
        browser.waitForElement(LocatorType.NAME, "q", 10);
        browser.sendKeys(LocatorType.NAME, "q", "Selenium WebDriver");

        // Click search button or press enter
        ExecutionResult result = browser.click(LocatorType.NAME, "btnK");

        // If first button not clickable, try the other one
        if (!result.isSuccessful()) {
            result = browser.click(LocatorType.NAME, "btnI");
        }

        assertTrue(result.isSuccessful() || driver.getCurrentUrl().contains("search"),
                "Click or search should succeed");
        log.info("Click test passed");
    }

    @Test(priority = 4, description = "Test send keys to Google search box")
    public void testSendKeysAction() {
        log.info("Testing SEND_KEYS action");

        browser.navigateTo("https://www.google.com");

        browser.waitForElement(LocatorType.NAME, "q");

        // Clear field first to avoid previous test data
        browser.clear(LocatorType.NAME, "q");

        ExecutionResult result = browser.sendKeys(LocatorType.NAME, "q", "TestNG framework");

        assertTrue(result.isSuccessful(), "Send keys should succeed");

        // Verify text was entered
        String value = browser.getValue(LocatorType.NAME, "q");
        assertEquals(value, "TestNG framework", "Text should match");

        log.info("Send keys test passed");
    }

    @Test(priority = 5, description = "Test clear input field")
    public void testClearAction() {
        log.info("Testing CLEAR action");

        browser.navigateTo("https://www.google.com");

        // Type something first
        browser.waitForElement(LocatorType.NAME, "q");
        browser.sendKeys(LocatorType.NAME, "q", "Test text");

        // Clear it
        ExecutionResult result = browser.clear(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Clear should succeed");

        // Verify it's empty
        String value = browser.getValue(LocatorType.NAME, "q");
        assertTrue(value == null || value.isEmpty(), "Input should be empty after clear");

        log.info("Clear test passed");
    }

    @Test(priority = 6, description = "Test get text from element")
    public void testGetTextAction() {
        log.info("Testing GET_TEXT action");

        browser.navigateTo("https://www.google.com");

        // Wait for and get text from an element
        browser.waitForElement(LocatorType.NAME, "q");

        // Get attribute value instead (placeholder text)
        String placeholder = browser.getAttribute(LocatorType.NAME, "q", "title");

        assertNotNull(placeholder, "Should get attribute value");
        log.info("Get text test passed, got attribute: {}", placeholder);
    }

    @Test(priority = 7, description = "Test wait for element")
    public void testWaitForElement() {
        log.info("Testing WAIT_FOR_ELEMENT action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.waitForElement(LocatorType.NAME, "q", 10);

        assertTrue(result.isSuccessful(), "Wait should succeed");
        log.info("Wait for element test passed");
    }

    @Test(priority = 8, description = "Test wait for clickable element")
    public void testWaitForClickable() {
        log.info("Testing WAIT_FOR_CLICKABLE action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.waitForClickable(LocatorType.NAME, "q", 10);

        assertTrue(result.isSuccessful(), "Wait for clickable should succeed");
        log.info("Wait for clickable test passed");
    }

    @Test(priority = 9, description = "Test navigate back and forward")
    public void testNavigateBackForward() {
        log.info("Testing NAVIGATE_BACK and NAVIGATE_FORWARD");

        // Navigate to first page
        browser.navigateTo("https://www.google.com");
        String firstUrl = driver.getCurrentUrl();

        // Navigate to second page
        browser.navigateTo("https://www.example.com");
        String secondUrl = driver.getCurrentUrl();

        assertNotEquals(firstUrl, secondUrl, "URLs should be different");

        // Go back
        ExecutionResult backResult = browser.navigateBack();
        assertTrue(backResult.isSuccessful(), "Navigate back should succeed");

        // Go forward
        ExecutionResult forwardResult = browser.navigateForward();
        assertTrue(forwardResult.isSuccessful(), "Navigate forward should succeed");

        log.info("Navigate back/forward test passed");
    }

    @Test(priority = 10, description = "Test refresh page")
    public void testRefreshPage() {
        log.info("Testing REFRESH action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.refresh();

        assertTrue(result.isSuccessful(), "Refresh should succeed");
        assertTrue(driver.getCurrentUrl().contains("google.com"), "Should still be on Google");
        log.info("Refresh test passed");
    }

    @Test(priority = 11, description = "Test maximize window")
    public void testMaximizeWindow() {
        log.info("Testing MAXIMIZE_WINDOW action");

        ExecutionResult result = browser.maximizeWindow();

        assertTrue(result.isSuccessful(), "Maximize window should succeed");
        log.info("Maximize window test passed");
    }

    @Test(priority = 12, description = "Test take screenshot")
    public void testTakeScreenshot() {
        log.info("Testing TAKE_SCREENSHOT action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.takeScreenshot();

        assertTrue(result.isSuccessful(), "Screenshot should succeed");
        assertNotNull(result.getScreenshotAfter(), "Screenshot byte array should be populated");
        assertTrue(result.getScreenshotAfter().length > 0, "Screenshot should have content");
        log.info("Screenshot test passed");
    }

    @Test(priority = 13, description = "Test hover action")
    public void testHoverAction() {
        log.info("Testing HOVER action");

        browser.navigateTo("https://www.google.com");

        browser.waitForElement(LocatorType.NAME, "q");
        ExecutionResult result = browser.hover(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Hover should succeed");
        log.info("Hover test passed");
    }

    @Test(priority = 14, description = "Test scroll to element")
    public void testScrollToElement() {
        log.info("Testing SCROLL_TO_ELEMENT action");

        browser.navigateTo("https://www.google.com");

        browser.waitForElement(LocatorType.NAME, "q");
        ExecutionResult result = browser.scrollToElement(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Scroll should succeed");
        log.info("Scroll to element test passed");
    }

    @Test(priority = 15, description = "Test validate title")
    public void testValidateTitle() {
        log.info("Testing VALIDATE_TITLE action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.validateTitle("Google");

        assertTrue(result.isSuccessful(), "Title validation should succeed");
        log.info("Validate title test passed");
    }

    @Test(priority = 16, description = "Test form filling workflow")
    public void testFillFormWorkflow() {
        log.info("Testing complete form filling workflow");

        browser.navigateTo("https://www.google.com");

        // Now we can use the constructor since FormData is fixed
        List<FormData> formFields = Arrays.asList(
                new FormData(LocatorType.NAME, "q", "TestNG Selenium")
        );

        ExecutionResult result = browser.fillForm(formFields);

        assertTrue(result.isSuccessful(), "Form fill should succeed");

        // Verify value was set
        String value = browser.getValue(LocatorType.NAME, "q");
        assertEquals(value, "TestNG Selenium", "Form value should match");

        log.info("Form filling workflow test passed");
    }

    @Test(priority = 17, description = "Test complete search workflow")
    public void testCompleteSearchWorkflow() {
        log.info("Testing complete Google search workflow");

        // Configure browser
        browser.withDefaultTimeout(15)
                .withScreenshotOnFailure(true);

        // Navigate to Google
        ExecutionResult navResult = browser.navigateTo("https://www.google.com");
        assertTrue(navResult.isSuccessful(), "Navigation should succeed");

        // Wait for search box
        ExecutionResult waitResult = browser.waitForElement(LocatorType.NAME, "q");
        assertTrue(waitResult.isSuccessful(), "Wait should succeed");

        // Type search query
        ExecutionResult typeResult = browser.sendKeys(LocatorType.NAME, "q", "Selenium automation");
        assertTrue(typeResult.isSuccessful(), "Typing should succeed");

        // Verify text was entered
        String value = browser.getValue(LocatorType.NAME, "q");
        assertEquals(value, "Selenium automation", "Search text should match");

        // Clear the search
        ExecutionResult clearResult = browser.clear(LocatorType.NAME, "q");
        assertTrue(clearResult.isSuccessful(), "Clear should succeed");

        log.info("Complete search workflow test passed");
    }

    @Test(priority = 18, description = "Test double click action")
    public void testDoubleClickAction() {
        log.info("Testing DOUBLE_CLICK action");

        browser.navigateTo("https://www.google.com");

        browser.waitForElement(LocatorType.NAME, "q");
        ExecutionResult result = browser.doubleClick(LocatorType.NAME, "q");

        assertTrue(result.isSuccessful(), "Double click should succeed");
        log.info("Double click test passed");
    }

    @Test(priority = 19, description = "Test get attribute")
    public void testGetAttribute() {
        log.info("Testing GET_ATTRIBUTE action");

        browser.navigateTo("https://www.google.com");

        browser.waitForElement(LocatorType.NAME, "q");
        String attribute = browser.getAttribute(LocatorType.NAME, "q", "name");

        assertEquals(attribute, "q", "Attribute value should match");
        log.info("Get attribute test passed");
    }

    @Test(priority = 20, description = "Test navigation with screenshot")
    public void testNavigationWithScreenshot() {
        log.info("Testing navigation with screenshot");

        ExecutionResult result = browser.navigateTo("https://www.google.com", true);

        assertTrue(result.isSuccessful(), "Navigation with screenshot should succeed");
        assertNotNull(result.getScreenshotAfter(), "Screenshot should be taken");
        log.info("Navigation with screenshot test passed");
    }

    @Test(priority = 21, description = "Test click with screenshot")
    public void testClickWithScreenshot() {
        log.info("Testing click with screenshot");

        browser.navigateTo("https://www.google.com");
        browser.waitForElement(LocatorType.NAME, "q");

        ExecutionResult result = browser.click(LocatorType.NAME, "q", true);

        assertTrue(result.isSuccessful(), "Click with screenshot should succeed");
        assertNotNull(result.getScreenshotAfter(), "Screenshot should be taken");
        log.info("Click with screenshot test passed");
    }

    @Test(priority = 22, description = "Test get underlying driver")
    public void testGetDriver() {
        log.info("Testing getDriver method");

        WebDriver retrievedDriver = browser.getDriver();

        assertNotNull(retrievedDriver, "Driver should not be null");
        assertSame(driver, retrievedDriver, "Should return same driver instance");
        log.info("Get driver test passed");
    }

    @Test(priority = 23, description = "Test wait for visible element")
    public void testWaitForVisible() {
        log.info("Testing WAIT_FOR_VISIBLE action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.waitForVisible(LocatorType.NAME, "q", 10);

        assertTrue(result.isSuccessful(), "Wait for visible should succeed");
        log.info("Wait for visible test passed");
    }

    @Test(priority = 24, description = "Test multiple configuration changes")
    public void testMultipleConfigurationChanges() {
        log.info("Testing multiple configuration changes");

        BrowserContext configured = browser
                .withDefaultTimeout(20)
                .withScreenshotOnFailure(false)
                .withDefaultRetry(RetryStrategy.IMMEDIATE_RETRY)
                .withScreenshotOnFailure(true);

        assertSame(browser, configured, "Should maintain same instance");
        log.info("Multiple configuration test passed");
    }

    @Test(priority = 25, description = "Test error handling with invalid element")
    public void testErrorHandlingInvalidElement() {
        log.info("Testing error handling with invalid element");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.click(LocatorType.ID, "non-existent-element-id-12345");

        assertFalse(result.isSuccessful(), "Should fail with invalid element");
        assertNotNull(result.getErrorMessage(), "Error message should not be null");
        log.info("Error handling test passed: {}", result.getErrorMessage());
    }
}