package com.restbusters.webdriver;

/**
 * @author amatsaylo on 10/11/25
 * @project qreasp
 */
import com.restbusters.webdriver.core.ActionExecutor;
import com.restbusters.webdriver.enums.ActionType;
import com.restbusters.webdriver.models.ExecutionResult;
import com.restbusters.webdriver.models.FormData;
import com.restbusters.webdriver.models.WebDriverState;
import com.restbusters.webdriver.utils.LocatorUtils;
import com.restbusters.webdriver.core.WebDriverHelper;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.Arrays;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Example test class demonstrating how to use the WebDriver framework.
 * This shows various usage patterns and best practices.
 */
public class WebDriverTest {

    private static final Logger log = LoggerFactory.getLogger(WebDriverTest.class);
    private WebDriver driver;
    private ActionExecutor executor;

    @BeforeClass
    public void setupClass() {
        log.info("=== Setting up test class ===");
        // Create driver using WebDriverHelper in headless mode
        driver = WebDriverHelper.createWebDriver("chrome", true);
        executor = new ActionExecutor();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        log.info("=== Tearing down test class ===");
        if (driver != null) {
            try {
                WebDriverHelper.quitDriver(driver);
            } catch (Exception e) {
                log.warn("Error closing driver: {}", e.getMessage());
            }
        }
    }

    @BeforeMethod
    public void beforeTest() {
        log.info("--- Starting new test ---");
    }

    @AfterMethod
    public void afterTest() {
        log.info("--- Test completed ---\n");
    }

    // ==================== BASIC NAVIGATION EXAMPLES ====================

    @Test(priority = 1, description = "Example 1: Simple navigation to a website")
    public void example1_SimpleNavigation() {
        log.info("EXAMPLE 1: Navigate to a website");

        // Create state for navigation
        WebDriverState state = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.example.com")
                .build();

        // Execute the action
        ExecutionResult result = executor.executeAction(state);

        // Verify success
        assertTrue(result.isSuccessful(), "Navigation should succeed");
        log.info("Result: {}", result.getResultValue());
        log.info("Current URL: {}", driver.getCurrentUrl());
    }

    @Test(priority = 2, description = "Example 2: Navigation with screenshot")
    public void example2_NavigationWithScreenshot() {
        log.info("EXAMPLE 2: Navigate and take screenshot");

        WebDriverState state = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .takeScreenshotAfter(true)  // Take screenshot after action
                .build();

        ExecutionResult result = executor.executeAction(state);

        assertTrue(result.isSuccessful());
        assertNotNull(result.getScreenshotAfter(), "Screenshot should be captured");
        log.info("Screenshot size: {} bytes", result.getScreenshotAfter().length);
    }

    // ==================== ELEMENT INTERACTION EXAMPLES ====================

    @Test(priority = 3, description = "Example 3: Find and click an element")
    public void example3_ClickElement() {
        log.info("EXAMPLE 3: Click on an element");

        // Navigate first
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        // Create locator for the search box
        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        // Click the search box
        WebDriverState clickState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.CLICK)
                .locator(searchBoxLocator)
                .build();

        ExecutionResult result = executor.executeAction(clickState);

        assertTrue(result.isSuccessful());
        log.info("Element clicked: {}", result.getResultValue());
    }

    @Test(priority = 4, description = "Example 4: Type text into input field")
    public void example4_SendKeys() {
        log.info("EXAMPLE 4: Type text into an input field");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        // Create locator and type text
        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        WebDriverState sendKeysState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.SEND_KEYS)
                .locator(searchBoxLocator)
                .value("Selenium WebDriver Tutorial")
                .build();

        ExecutionResult result = executor.executeAction(sendKeysState);

        assertTrue(result.isSuccessful());
        log.info("Typed: {}", result.getResultValue());
    }

    @Test(priority = 5, description = "Example 5: Get text from an element")
    public void example5_GetText() {
        log.info("EXAMPLE 5: Extract text from an element");

        // Navigate to example.com
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.example.com")
                .build();
        executor.executeAction(navState);

        // Get text from h1 element
        Map<String, String> h1Locator = LocatorUtils.createLocatorMap("tagName", "h1");

        WebDriverState getTextState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.GET_TEXT)
                .locator(h1Locator)
                .build();

        ExecutionResult result = executor.executeAction(getTextState);

        assertTrue(result.isSuccessful());
        log.info("Text extracted: '{}'", result.getResultValue());
        assertNotNull(result.getResultValue());
    }

    // ==================== ADVANCED LOCATOR EXAMPLES ====================

    @Test(priority = 6, description = "Example 6: Different locator strategies")
    public void example6_DifferentLocators() {
        log.info("EXAMPLE 6: Using different locator strategies");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.example.com")
                .build();
        executor.executeAction(navState);

        // Try different locator types
        Map<String, String> xpathLocator = LocatorUtils.createLocatorMap("xpath", "//h1");
        Map<String, String> cssLocator = LocatorUtils.createLocatorMap("css", "h1");
        Map<String, String> tagLocator = LocatorUtils.createLocatorMap("tagName", "h1");

        // Use xpath locator
        WebDriverState xpathState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.GET_TEXT)
                .locator(xpathLocator)
                .build();

        ExecutionResult result = executor.executeAction(xpathState);

        assertTrue(result.isSuccessful());
        log.info("Found element using xpath: {}", result.getResultValue());
    }

    // ==================== WAIT EXAMPLES ====================

    @Test(priority = 7, description = "Example 7: Wait for element to appear")
    public void example7_WaitForElement() {
        log.info("EXAMPLE 7: Wait for element");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        // Wait for search box to appear
        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        WebDriverState waitState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.WAIT_FOR_ELEMENT)
                .locator(searchBoxLocator)
                .timeoutInSeconds(10)
                .build();

        ExecutionResult result = executor.executeAction(waitState);

        assertTrue(result.isSuccessful());
        log.info("Element found: {}", result.getResultValue());
    }

    @Test(priority = 8, description = "Example 8: Wait for element to be clickable")
    public void example8_WaitForClickable() {
        log.info("EXAMPLE 8: Wait for element to be clickable");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        WebDriverState waitState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.WAIT_FOR_ELEMENT_CLICKABLE)
                .locator(searchBoxLocator)
                .timeoutInSeconds(10)
                .build();

        ExecutionResult result = executor.executeAction(waitState);

        assertTrue(result.isSuccessful());
        log.info("Element is clickable: {}", result.getResultValue());
    }

    // ==================== FORM FILLING EXAMPLE ====================

    @Test(priority = 9, description = "Example 9: Fill multiple form fields")
    public void example9_FillForm() {
        log.info("EXAMPLE 9: Fill a form with multiple fields");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        // Create form data for multiple fields
        FormData searchField = FormData.builder()
                .fieldName("search")
                .locator(LocatorUtils.createLocatorMap("name", "q"))
                .value("Test automation")
                .clearBeforeType(true)
                .build();

        WebDriverState fillFormState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.FILL_FORM)
                .formDataList(Arrays.asList(searchField))
                .build();

        ExecutionResult result = executor.executeAction(fillFormState);

        assertTrue(result.isSuccessful());
        log.info("Form filled: {}", result.getResultValue());
    }

    // ==================== MOUSE ACTIONS EXAMPLES ====================

    @Test(priority = 10, description = "Example 10: Double click on element")
    public void example10_DoubleClick() {
        log.info("EXAMPLE 10: Double click an element");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        WebDriverState doubleClickState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.DOUBLE_CLICK)
                .locator(searchBoxLocator)
                .build();

        ExecutionResult result = executor.executeAction(doubleClickState);

        assertTrue(result.isSuccessful());
        log.info("Double clicked: {}", result.getResultValue());
    }

    @Test(priority = 11, description = "Example 11: Hover over an element")
    public void example11_HoverElement() {
        log.info("EXAMPLE 11: Hover over an element");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .build();
        executor.executeAction(navState);

        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");

        WebDriverState hoverState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.HOVER)
                .locator(searchBoxLocator)
                .build();

        ExecutionResult result = executor.executeAction(hoverState);

        assertTrue(result.isSuccessful());
        log.info("Hovered: {}", result.getResultValue());
    }

    // ==================== WINDOW MANAGEMENT EXAMPLES ====================

    @Test(priority = 12, description = "Example 12: Maximize window")
    public void example12_MaximizeWindow() {
        log.info("EXAMPLE 12: Maximize browser window");

        WebDriverState state = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.MAXIMIZE_WINDOW)
                .build();

        ExecutionResult result = executor.executeAction(state);

        assertTrue(result.isSuccessful());
        log.info("Window maximized: {}", result.getResultValue());
    }

    @Test(priority = 13, description = "Example 13: Refresh page")
    public void example13_RefreshPage() {
        log.info("EXAMPLE 13: Refresh the current page");

        // Navigate first
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.example.com")
                .build();
        executor.executeAction(navState);

        // Refresh
        WebDriverState refreshState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.REFRESH)
                .build();

        ExecutionResult result = executor.executeAction(refreshState);

        assertTrue(result.isSuccessful());
        log.info("Page refreshed: {}", result.getResultValue());
    }

    // ==================== ERROR HANDLING EXAMPLE ====================

    @Test(priority = 14, description = "Example 14: Handle errors gracefully")
    public void example14_ErrorHandling() {
        log.info("EXAMPLE 14: Error handling demonstration");

        // Navigate
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.example.com")
                .build();
        executor.executeAction(navState);

        // Try to find non-existent element
        Map<String, String> invalidLocator = LocatorUtils.createLocatorMap("id", "non-existent-element-12345");

        WebDriverState clickState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.CLICK)
                .locator(invalidLocator)
                .takeScreenshotOnFailure(true)  // Take screenshot on failure
                .build();

        ExecutionResult result = executor.executeAction(clickState);

        assertFalse(result.isSuccessful(), "Should fail for non-existent element");
        assertNotNull(result.getErrorMessage(), "Should have error message");
        assertNotNull(result.getScreenshotAfter(), "Should have failure screenshot");

        log.info("Error handled gracefully: {}", result.getErrorMessage());
        log.info("Failure screenshot captured: {} bytes", result.getScreenshotAfter().length);
    }

    // ==================== COMPLETE WORKFLOW EXAMPLE ====================

    @Test(priority = 15, description = "Example 15: Complete workflow - Search on Google")
    public void example15_CompleteWorkflow() {
        log.info("EXAMPLE 15: Complete workflow - Google search");

        // Step 1: Navigate to Google
        log.info("Step 1: Navigate to Google");
        WebDriverState navState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.NAVIGATE_TO)
                .url("https://www.google.com")
                .takeScreenshotAfter(true)
                .build();
        ExecutionResult navResult = executor.executeAction(navState);
        assertTrue(navResult.isSuccessful());

        // Step 2: Wait for search box
        log.info("Step 2: Wait for search box");
        Map<String, String> searchBoxLocator = LocatorUtils.createLocatorMap("name", "q");
        WebDriverState waitState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.WAIT_FOR_ELEMENT_CLICKABLE)
                .locator(searchBoxLocator)
                .timeoutInSeconds(10)
                .build();
        ExecutionResult waitResult = executor.executeAction(waitState);
        assertTrue(waitResult.isSuccessful());

        // Step 3: Clear search box (if needed)
        log.info("Step 3: Clear search box");
        WebDriverState clearState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.CLEAR)
                .locator(searchBoxLocator)
                .build();
        ExecutionResult clearResult = executor.executeAction(clearState);
        assertTrue(clearResult.isSuccessful());

        // Step 4: Type search query
        log.info("Step 4: Type search query");
        WebDriverState typeState = WebDriverState.builder()
                .webDriver(driver)
                .actionType(ActionType.SEND_KEYS)
                .locator(searchBoxLocator)
                .value("Selenium WebDriver automation")
                .takeScreenshotAfter(true)
                .build();
        ExecutionResult typeResult = executor.executeAction(typeState);
        assertTrue(typeResult.isSuccessful());

        log.info("Complete workflow executed successfully!");
        log.info("Navigation time: {}", navResult.getExecutionDuration());
        log.info("Total screenshots taken: 2");
    }

    // ==================== HELPER METHODS USAGE EXAMPLE ====================

    @Test(priority = 16, description = "Example 16: Using WebDriverHelper utilities")
    public void example16_HelperUtilities() {
        log.info("EXAMPLE 16: Using WebDriverHelper utilities");

        // Navigate using helper
        boolean navigated = WebDriverHelper.navigateTo(driver, "https://www.example.com");
        assertTrue(navigated, "Navigation should succeed");

        // Get page title
        String title = WebDriverHelper.getPageTitle(driver);
        log.info("Page title: {}", title);
        assertNotNull(title);

        // Get current URL
        String url = WebDriverHelper.getCurrentUrl(driver);
        log.info("Current URL: {}", url);
        assertTrue(url.contains("example.com"));

        // Check if driver is active
        boolean active = WebDriverHelper.isDriverActive(driver);
        assertTrue(active, "Driver should be active");

        // Refresh page
        boolean refreshed = WebDriverHelper.refresh(driver);
        assertTrue(refreshed, "Refresh should succeed");

        log.info("All helper utilities work correctly!");
    }
}