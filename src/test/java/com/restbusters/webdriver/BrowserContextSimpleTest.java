package com.restbusters.webdriver;

import com.restbusters.webdriver.core.DriverManager;
import com.restbusters.webdriver.enums.RetryStrategy;
import com.restbusters.webdriver.facade.BrowserContext;
import com.restbusters.webdriver.models.ExecutionResult;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * Simplified TestNG test class for BrowserContext functionality.
 * Tests basic navigation and configuration without element interactions.
 */
public class BrowserContextSimpleTest {

    private static final Logger log = LoggerFactory.getLogger(BrowserContextSimpleTest.class);
    private static WebDriver driver;
    private BrowserContext browser;

    @BeforeSuite
    public void setupDriver() {
        log.info("Setting up WebDriver for test suite");
        driver = DriverManager.createChromeDriver();
    }

    @AfterSuite
    public void tearDownDriver() {
        log.info("Tearing down WebDriver");
        DriverManager.quitDriver(driver);
    }

    @BeforeMethod
    public void setup() {
        browser = BrowserContext.with(driver);
    }

    @Test(priority = 1, description = "Test create BrowserContext")
    public void testCreateBrowserContext() {
        log.info("Testing BrowserContext creation");

        BrowserContext context = BrowserContext.with(driver);

        assertNotNull(context, "BrowserContext should not be null");
        assertSame(driver, context.getDriver(), "Should return same driver");
        log.info("BrowserContext creation test passed");
    }

    @Test(priority = 2, description = "Test navigate to Google")
    public void testNavigateToGoogle() {
        log.info("Testing navigation to Google");

        ExecutionResult result = browser.navigateTo("https://www.google.com");

        assertTrue(result.isSuccessful(), "Navigation should succeed");
        assertTrue(driver.getCurrentUrl().contains("google.com"), "Should be on Google");
        log.info("Navigate test passed");
    }

    @Test(priority = 3, description = "Test navigate to Example.com")
    public void testNavigateToExample() {
        log.info("Testing navigation to Example.com");

        ExecutionResult result = browser.navigateTo("https://www.example.com");

        assertTrue(result.isSuccessful(), "Navigation should succeed");
        assertTrue(driver.getCurrentUrl().contains("example.com"), "Should be on Example");
        log.info("Navigate to Example test passed");
    }

    @Test(priority = 4, description = "Test fluent configuration")
    public void testFluentConfiguration() {
        log.info("Testing fluent API configuration");

        BrowserContext configuredBrowser = browser
                .withDefaultTimeout(15)
                .withScreenshotOnFailure(true)
                .withDefaultRetry(RetryStrategy.NO_RETRY);

        assertSame(browser, configuredBrowser, "Should return same instance for fluent API");
        log.info("Fluent configuration test passed");
    }

    @Test(priority = 5, description = "Test navigate back and forward")
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

    @Test(priority = 6, description = "Test refresh page")
    public void testRefreshPage() {
        log.info("Testing REFRESH action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.refresh();

        assertTrue(result.isSuccessful(), "Refresh should succeed");
        assertTrue(driver.getCurrentUrl().contains("google.com"), "Should still be on Google");
        log.info("Refresh test passed");
    }

    @Test(priority = 7, description = "Test maximize window")
    public void testMaximizeWindow() {
        log.info("Testing MAXIMIZE_WINDOW action");

        ExecutionResult result = browser.maximizeWindow();

        assertTrue(result.isSuccessful(), "Maximize window should succeed");
        log.info("Maximize window test passed");
    }

    @Test(priority = 8, description = "Test take screenshot")
    public void testTakeScreenshot() {
        log.info("Testing TAKE_SCREENSHOT action");

        browser.navigateTo("https://www.google.com");

        ExecutionResult result = browser.takeScreenshot();

        assertTrue(result.isSuccessful(), "Screenshot should succeed");
        // Note: Screenshot is saved to disk, not returned in screenshotAfter byte array
        log.info("Screenshot test passed");
    }

    @Test(priority = 9, description = "Test navigation with screenshot")
    public void testNavigationWithScreenshot() {
        log.info("Testing navigation with screenshot");

        ExecutionResult result = browser.navigateTo("https://www.google.com", true);

        assertTrue(result.isSuccessful(), "Navigation with screenshot should succeed");
        // Note: Screenshot is saved to disk, not returned in screenshotAfter byte array
        log.info("Navigation with screenshot test passed");
    }

    @Test(priority = 10, description = "Test multiple navigation sequence")
    public void testMultipleNavigationSequence() {
        log.info("Testing multiple navigation sequence");

        ExecutionResult result1 = browser.navigateTo("https://www.google.com");
        assertTrue(result1.isSuccessful(), "First navigation should succeed");

        ExecutionResult result2 = browser.navigateTo("https://www.example.com");
        assertTrue(result2.isSuccessful(), "Second navigation should succeed");

        ExecutionResult result3 = browser.navigateTo("https://www.google.com");
        assertTrue(result3.isSuccessful(), "Third navigation should succeed");

        log.info("Multiple navigation sequence test passed");
    }

    @Test(priority = 12, description = "Test configuration persistence")
    public void testConfigurationPersistence() {
        log.info("Testing configuration persistence");

        browser.withDefaultTimeout(20)
                .withScreenshotOnFailure(false)
                .withDefaultRetry(RetryStrategy.IMMEDIATE_RETRY);

        // Navigate after configuration
        ExecutionResult result = browser.navigateTo("https://www.google.com");

        assertTrue(result.isSuccessful(), "Navigation with custom config should succeed");
        log.info("Configuration persistence test passed");
    }

    @Test(priority = 13, description = "Test get driver")
    public void testGetDriver() {
        log.info("Testing getDriver method");

        WebDriver retrievedDriver = browser.getDriver();

        assertNotNull(retrievedDriver, "Driver should not be null");
        assertSame(driver, retrievedDriver, "Should return same driver instance");
        log.info("Get driver test passed");
    }
}