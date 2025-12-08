# WebDriver Usage Guide - Raw vs Wrapper Approach

## Overview

This guide demonstrates two approaches to Selenium WebDriver automation:

1. **Raw WebDriver** - Direct Selenium API usage (verbose, complex)
2. **BrowserContext Wrapper** - Simplified fluent API (clean, maintainable)

---

## 📋 Table of Contents

- [Setup Comparison](#setup-comparison)
- [Simple Actions](#simple-actions)
- [Wait Operations](#wait-operations)
- [Form Handling](#form-handling)
- [Error Handling](#error-handling)
- [Screenshot Management](#screenshot-management)
- [Retry Logic](#retry-logic)
- [Complete Test Examples](#complete-test-examples)

---

## Setup Comparison

### ❌ Raw WebDriver Approach

```java
public class RawWebDriverTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    
    @BeforeSuite
    public void setup() {
        // Manual driver setup
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
    }
    
    @AfterSuite
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### ✅ BrowserContext Wrapper Approach

```java
public class BrowserContextTest {
    private static WebDriver driver;
    private BrowserContext browser;
    
    @BeforeSuite
    public void setup() {
        // Simple driver creation
        driver = DriverManager.createChromeDriver();
    }
    
    @AfterSuite
    public void teardown() {
        DriverManager.quitDriver(driver);
    }
    
    @BeforeMethod
    public void setupTest() {
        // Clean browser instance per test
        browser = BrowserContext.with(driver);
    }
}
```

**✨ Benefits:** 
- 60% less boilerplate code
- Automatic configuration
- Cleaner test setup

---

## Simple Actions

### ❌ Raw WebDriver - Click a Button

```java
@Test
public void testClickButton() {
    // Navigate
    driver.get("https://www.google.com");
    
    // Wait for element
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement searchBox = wait.until(
        ExpectedConditions.elementToBeClickable(By.name("q"))
    );
    
    // Click
    try {
        searchBox.click();
        System.out.println("Click successful");
    } catch (Exception e) {
        // Take screenshot manually
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        System.err.println("Click failed: " + e.getMessage());
        throw e;
    }
}
```

**Lines of code:** ~20 lines

### ✅ BrowserContext Wrapper - Click a Button

```java
@Test
public void testClickButton() {
    browser.navigateTo("https://www.google.com");
    
    ExecutionResult result = browser.click(LocatorType.NAME, "q");
    
    assertTrue(result.isSuccessful(), "Click should succeed");
}
```

**Lines of code:** ~5 lines

**✨ Benefits:**
- 75% less code
- Built-in error handling
- Automatic waits
- Execution result tracking

---

## Wait Operations

### ❌ Raw WebDriver - Wait for Element

```java
@Test
public void testWaitForElement() {
    driver.get("https://www.google.com");
    
    // Manual wait configuration
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    try {
        // Wait for element to be present
        WebElement element = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.name("q"))
        );
        
        // Wait for element to be visible
        wait.until(ExpectedConditions.visibilityOf(element));
        
        // Wait for element to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(element));
        
        System.out.println("Element is ready");
    } catch (TimeoutException e) {
        System.err.println("Element not found within timeout");
        throw e;
    }
}
```

**Lines of code:** ~20 lines

### ✅ BrowserContext Wrapper - Wait for Element

```java
@Test
public void testWaitForElement() {
    browser.navigateTo("https://www.google.com");
    
    // Wait for presence
    ExecutionResult result1 = browser.waitForElement(LocatorType.NAME, "q");
    
    // Wait for clickable
    ExecutionResult result2 = browser.waitForClickable(LocatorType.NAME, "q");
    
    // Wait for visible
    ExecutionResult result3 = browser.waitForVisible(LocatorType.NAME, "q");
    
    assertTrue(result1.isSuccessful(), "Element should be present");
    assertTrue(result2.isSuccessful(), "Element should be clickable");
    assertTrue(result3.isSuccessful(), "Element should be visible");
}
```

**Lines of code:** ~11 lines

**✨ Benefits:**
- Cleaner syntax
- Consistent API
- Better error messages
- Execution tracking

---

## Form Handling

### ❌ Raw WebDriver - Fill Form

```java
@Test
public void testFillForm() {
    driver.get("https://www.google.com");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    try {
        // Find search box
        WebElement searchBox = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.name("q"))
        );
        
        // Clear existing text
        searchBox.clear();
        
        // Wait a bit for clear to complete
        Thread.sleep(100);
        
        // Type text
        searchBox.sendKeys("Selenium WebDriver");
        
        // Wait for submit button
        WebElement submitBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.name("btnK"))
        );
        
        // Submit form
        submitBtn.click();
        
        // Wait for results page
        wait.until(ExpectedConditions.urlContains("search"));
        
        System.out.println("Form submitted successfully");
        
    } catch (Exception e) {
        System.err.println("Form fill failed: " + e.getMessage());
        // Manual screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        throw new RuntimeException(e);
    }
}
```

**Lines of code:** ~35 lines

### ✅ BrowserContext Wrapper - Fill Form

```java
@Test
public void testFillForm() {
    browser.navigateTo("https://www.google.com");
    
    // Single field
    browser.sendKeys(LocatorType.NAME, "q", "Selenium WebDriver");
    browser.click(LocatorType.NAME, "btnK");
    
    // Or use form helper
    List<FormData> formFields = Arrays.asList(
        new FormData(LocatorType.NAME, "q", "Selenium WebDriver")
    );
    
    ExecutionResult result = browser.fillForm(formFields);
    assertTrue(result.isSuccessful(), "Form should be filled");
}
```

**Lines of code:** ~13 lines

**✨ Benefits:**
- 60% less code
- Automatic clear before type
- Built-in waits
- Form validation support

---

## Error Handling

### ❌ Raw WebDriver - Error Handling

```java
@Test
public void testWithErrorHandling() {
    try {
        driver.get("https://www.google.com");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("nonexistent"))
        );
        
        element.click();
        
    } catch (TimeoutException e) {
        System.err.println("Timeout: " + e.getMessage());
        takeScreenshot("timeout_error");
        throw e;
        
    } catch (NoSuchElementException e) {
        System.err.println("Element not found: " + e.getMessage());
        takeScreenshot("element_not_found");
        throw e;
        
    } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        takeScreenshot("unexpected_error");
        throw e;
    }
}

private void takeScreenshot(String filename) {
    try {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = "screenshots/" + filename + "_" + System.currentTimeMillis() + ".png";
        FileUtils.copyFile(screenshot, new File(path));
        System.out.println("Screenshot saved: " + path);
    } catch (IOException e) {
        System.err.println("Failed to save screenshot: " + e.getMessage());
    }
}
```

**Lines of code:** ~35 lines

### ✅ BrowserContext Wrapper - Error Handling

```java
@Test
public void testWithErrorHandling() {
    browser.navigateTo("https://www.google.com");
    
    // Configure automatic screenshot on failure
    browser.withScreenshotOnFailure(true);
    
    ExecutionResult result = browser.click(LocatorType.ID, "nonexistent");
    
    // Check result
    if (!result.isSuccessful()) {
        System.out.println("Error: " + result.getErrorMessage());
        
        // Screenshot automatically captured
        byte[] screenshot = result.getScreenshotAfter();
        assertNotNull(screenshot, "Screenshot should be captured on failure");
    }
}
```

**Lines of code:** ~12 lines

**✨ Benefits:**
- Automatic error capture
- Built-in screenshot on failure
- Structured error information
- No manual try-catch needed

---

## Screenshot Management

### ❌ Raw WebDriver - Screenshots

```java
@Test
public void testWithScreenshots() throws IOException {
    // Take screenshot before
    File beforeScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String beforePath = "screenshots/before_" + System.currentTimeMillis() + ".png";
    FileUtils.copyFile(beforeScreenshot, new File(beforePath));
    System.out.println("Before screenshot: " + beforePath);
    
    // Perform action
    driver.get("https://www.google.com");
    WebElement searchBox = driver.findElement(By.name("q"));
    searchBox.sendKeys("test");
    
    // Take screenshot after
    File afterScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String afterPath = "screenshots/after_" + System.currentTimeMillis() + ".png";
    FileUtils.copyFile(afterScreenshot, new File(afterPath));
    System.out.println("After screenshot: " + afterPath);
    
    // Compare or analyze screenshots manually
}
```

**Lines of code:** ~18 lines

### ✅ BrowserContext Wrapper - Screenshots

```java
@Test
public void testWithScreenshots() {
    browser.navigateTo("https://www.google.com");
    
    // Take explicit screenshot
    ExecutionResult result = browser.takeScreenshot();
    byte[] screenshot = result.getScreenshotAfter();
    
    // Or take screenshot with action
    ExecutionResult clickResult = browser.click(LocatorType.NAME, "q", true);
    byte[] actionScreenshot = clickResult.getScreenshotAfter();
    
    // Screenshots are automatically saved to disk and returned as byte arrays
    assertNotNull(screenshot);
    assertNotNull(actionScreenshot);
}
```

**Lines of code:** ~11 lines

**✨ Benefits:**
- Automatic file management
- Both disk and byte array output
- Screenshots on any action
- Consistent naming

---

## Retry Logic

### ❌ Raw WebDriver - Manual Retry

```java
@Test
public void testWithRetry() {
    driver.get("https://www.google.com");
    
    int maxAttempts = 3;
    int attempt = 0;
    boolean success = false;
    Exception lastException = null;
    
    while (attempt < maxAttempts && !success) {
        try {
            attempt++;
            System.out.println("Attempt " + attempt + " of " + maxAttempts);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("q"))
            );
            
            element.click();
            success = true;
            System.out.println("Success on attempt " + attempt);
            
        } catch (Exception e) {
            lastException = e;
            System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
            
            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(1000); // Wait before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    if (!success) {
        throw new RuntimeException("Failed after " + maxAttempts + " attempts", lastException);
    }
}
```

**Lines of code:** ~40 lines

### ✅ BrowserContext Wrapper - Built-in Retry

```java
@Test
public void testWithRetry() {
    browser.navigateTo("https://www.google.com");
    
    // Configure retry strategy
    browser.withDefaultRetry(RetryStrategy.IMMEDIATE_RETRY); // 3 attempts, no delay
    // Or: RetryStrategy.LINEAR_BACKOFF (3 attempts, 1s delay)
    // Or: RetryStrategy.EXPONENTIAL_BACKOFF (5 attempts, exponential delay)
    
    ExecutionResult result = browser.click(LocatorType.NAME, "q");
    
    assertTrue(result.isSuccessful(), "Should succeed after retries");
}
```

**Lines of code:** ~8 lines

**✨ Benefits:**
- 80% less code
- Multiple retry strategies
- Automatic backoff
- No manual loop management

---

## Complete Test Examples

### ❌ Raw WebDriver - Complete Test

```java
package com.example.tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.testng.Assert.*;

public class RawWebDriverTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    
    @BeforeSuite
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
    }
    
    @AfterSuite
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    public void testGoogleSearch() {
        try {
            // Navigate
            driver.get("https://www.google.com");
            
            // Wait for search box
            WebElement searchBox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("q"))
            );
            
            // Type search term
            searchBox.clear();
            searchBox.sendKeys("Selenium WebDriver");
            
            // Wait for search button
            WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("btnK"))
            );
            
            // Take screenshot before click
            takeScreenshot("before_search");
            
            // Click search
            searchButton.click();
            
            // Wait for results
            wait.until(ExpectedConditions.urlContains("search"));
            
            // Take screenshot after
            takeScreenshot("after_search");
            
            // Verify
            assertTrue(driver.getCurrentUrl().contains("search"), 
                "Should be on search results page");
            
        } catch (Exception e) {
            takeScreenshot("error");
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
        }
    }
    
    private void takeScreenshot(String name) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = "screenshots/" + name + "_" + System.currentTimeMillis() + ".png";
            FileUtils.copyFile(screenshot, new File(path));
            System.out.println("Screenshot: " + path);
        } catch (IOException e) {
            System.err.println("Screenshot failed: " + e.getMessage());
        }
    }
}
```

**Total lines:** ~85 lines

---

### ✅ BrowserContext Wrapper - Complete Test

```java
package com.restbusters.webdriver;

import com.restbusters.webdriver.core.DriverManager;
import com.restbusters.webdriver.enums.LocatorType;
import com.restbusters.webdriver.facade.BrowserContext;
import com.restbusters.webdriver.models.ExecutionResult;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class BrowserContextTest {
    
    private static WebDriver driver;
    private BrowserContext browser;
    
    @BeforeSuite
    public void setup() {
        driver = DriverManager.createChromeDriver();
    }
    
    @AfterSuite
    public void teardown() {
        DriverManager.quitDriver(driver);
    }
    
    @BeforeMethod
    public void setupTest() {
        browser = BrowserContext.with(driver);
    }
    
    @Test
    public void testGoogleSearch() {
        // Navigate
        browser.navigateTo("https://www.google.com");
        
        // Type search term (auto-clears, auto-waits)
        browser.sendKeys(LocatorType.NAME, "q", "Selenium WebDriver");
        
        // Click search (auto-waits, auto-screenshot)
        ExecutionResult result = browser.click(LocatorType.NAME, "btnK", true);
        
        // Verify
        assertTrue(result.isSuccessful(), "Search should succeed");
        assertNotNull(result.getScreenshotAfter(), "Screenshot should be captured");
        assertTrue(driver.getCurrentUrl().contains("search"), 
            "Should be on search results page");
    }
}
```

**Total lines:** ~40 lines

**✨ Benefits:**
- 50% less code
- Cleaner, more readable
- Built-in best practices
- Better maintainability

---

## Code Comparison Summary

| Feature | Raw WebDriver | BrowserContext | Code Reduction |
|---------|---------------|----------------|----------------|
| Setup | 25 lines | 10 lines | 60% |
| Click Action | 20 lines | 5 lines | 75% |
| Wait Operations | 20 lines | 11 lines | 45% |
| Form Handling | 35 lines | 13 lines | 63% |
| Error Handling | 35 lines | 12 lines | 66% |
| Screenshots | 18 lines | 11 lines | 39% |
| Retry Logic | 40 lines | 8 lines | 80% |
| **Complete Test** | **85 lines** | **40 lines** | **53%** |

---

## Key Benefits of BrowserContext Wrapper

### 🎯 Simplicity
- **50-80% less code** for common operations
- Fluent, chainable API
- No boilerplate

### 🛡️ Reliability  
- Built-in waits
- Automatic retries
- Intelligent error handling

### 📸 Debugging
- Automatic screenshots on failure
- Execution result tracking
- Detailed error information

### 🔧 Maintainability
- Consistent API across all actions
- Easy to update (change once, apply everywhere)
- Self-documenting code

### 🚀 Productivity
- Write tests faster
- Less code to maintain
- Focus on test logic, not WebDriver mechanics

---

## When to Use Each Approach

### Use Raw WebDriver When:
- ❌ You need very specific WebDriver features not wrapped
- ❌ You're doing low-level browser manipulation
- ❌ You need complete control over timing

### Use BrowserContext Wrapper When:
- ✅ Writing standard UI tests (95% of use cases)
- ✅ You want maintainable test code
- ✅ You need built-in error handling
- ✅ You want consistent best practices
- ✅ You value productivity over control

---

## Getting Started

### Add Dependencies (build.gradle):

```gradle
dependencies {
    testImplementation 'org.testng:testng:7.8.0'
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.6.2'
}
```

### Create Your First Test:

```java
import com.restbusters.webdriver.core.DriverManager;
import com.restbusters.webdriver.facade.BrowserContext;
import com.restbusters.webdriver.enums.LocatorType;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

public class MyFirstTest {
    private static WebDriver driver;
    private BrowserContext browser;
    
    @BeforeSuite
    public void setup() {
        driver = DriverManager.createChromeDriver();
    }
    
    @AfterSuite
    public void teardown() {
        DriverManager.quitDriver(driver);
    }
    
    @BeforeMethod
    public void setupTest() {
        browser = BrowserContext.with(driver);
    }
    
    @Test
    public void myFirstTest() {
        browser.navigateTo("https://www.google.com");
        var result = browser.sendKeys(LocatorType.NAME, "q", "Hello World!");
        assertTrue(result.isSuccessful());
    }
}
```

### Run Your Test:

```bash
./gradlew test --tests MyFirstTest
```

---

## Conclusion

The **BrowserContext wrapper** provides a clean, maintainable, and productive way to write Selenium tests. By abstracting common patterns and providing built-in best practices, it allows you to:

- ✅ Write **50-80% less code**
- ✅ Focus on **test logic** instead of WebDriver mechanics
- ✅ Get **automatic error handling** and **retry logic**
- ✅ Maintain **consistent code** across your test suite

**Start using BrowserContext today** and experience the difference! 🚀

---

## Additional Resources

- [BrowserContext API Documentation](./API_REFERENCE.md)
- [Test Examples](./src/test/java/com/restbusters/webdriver/facade/)
- [Framework Architecture](./ARCHITECTURE.md)
- [Troubleshooting Guide](./TROUBLESHOOTING.md)

---

**Happy Testing!** 🎉
