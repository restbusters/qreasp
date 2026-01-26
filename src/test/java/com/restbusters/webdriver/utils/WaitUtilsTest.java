package com.restbusters.webdriver.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for WaitUtils utility class.
 *
 * Note: Most WaitUtils methods require a WebDriver instance and are tested
 * via integration tests in BrowserContextTest. These unit tests cover
 * the methods that don't require WebDriver.
 */
public class WaitUtilsTest {

    @Test(description = "Test sleep does not throw exception")
    public void testSleepDoesNotThrow() {
        long start = System.currentTimeMillis();

        // Should not throw any exception
        WaitUtils.sleep(100);

        long elapsed = System.currentTimeMillis() - start;

        // Should have slept for approximately 100ms (allow some tolerance)
        assertTrue(elapsed >= 90, "Should have slept for at least 90ms");
        assertTrue(elapsed < 200, "Should not have slept for more than 200ms");
    }

    @Test(description = "Test sleep with zero milliseconds")
    public void testSleepWithZeroMilliseconds() {
        long start = System.currentTimeMillis();

        WaitUtils.sleep(0);

        long elapsed = System.currentTimeMillis() - start;

        // Should complete almost immediately
        assertTrue(elapsed < 50, "Sleep(0) should complete quickly");
    }

    @Test(description = "Test sleep with small value")
    public void testSleepWithSmallValue() {
        long start = System.currentTimeMillis();

        WaitUtils.sleep(50);

        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed >= 40, "Should have slept for at least 40ms");
    }

    /**
     * Note: The following methods require WebDriver and are tested in integration tests:
     * - waitForElement
     * - waitForElementClickable
     * - waitForElementVisible
     * - waitForElementInvisible
     * - waitForTextPresent
     * - waitForCondition
     *
     * See BrowserContextTest and BrowserContextSimpleTest for WebDriver-based tests.
     */
}
